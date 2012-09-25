package coder36.sbent.xml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.batch.item.xml.StaxUtils;
import org.springframework.batch.item.xml.stax.DefaultFragmentEventReader;
import org.springframework.batch.item.xml.stax.FragmentEventReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Item reader for reading XML input based on StAX.
 * 
 * It extracts fragments from the input XML document which correspond to records
 * for processing. The fragments are wrapped with StartDocument and EndDocument
 * events so that the fragments can be further processed like standalone XML
 * documents.
 * 
 * The implementation is *not* thread-safe.
 * 
 * 
 * Mark Middleton: Updated to support a map of ellementName's->UnMarshaller's.  This allows
 * the Reader to support multiple XML fragment types. 
 * 
 * @author Robert Kasanicky
 */
public class MultiStaxEventItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements ResourceAwareItemReaderItemStream<T>, InitializingBean {

	private static final Log logger = LogFactory.getLog(MultiStaxEventItemReader.class);

	private FragmentEventReader fragmentReader;

	private XMLEventReader eventReader;

	private Unmarshaller unmarshaller;
	
	
	private Map<String, Unmarshaller> unmarshallerMap;
	private Map<String, String> namespaceMap = new HashMap<String,String>();	

	private Resource resource;

	private InputStream inputStream;

	private String fragmentRootElementName;

	private boolean noInput;

	private boolean strict = true;

	public MultiStaxEventItemReader() {
		setName(ClassUtils.getShortName(MultiStaxEventItemReader.class));
	}

	/**
	 * In strict mode the reader will throw an exception on
	 * {@link #open(org.springframework.batch.item.ExecutionContext)} if the
	 * input resource does not exist.
	 * @param strict false by default
	 */
	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * @param unmarshaller maps xml fragments corresponding to records to
	 * objects
	 */
	public void setUnmarshallerMap(Map<String,Unmarshaller> m) {
		unmarshallerMap = m;
	}

	/**
	 * Ensure that all required dependencies for the ItemReader to run are
	 * provided after all properties have been set.
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 * @throws IllegalArgumentException if the Resource, FragmentDeserializer or
	 * FragmentRootElementName is null, or if the root element is empty.
	 * @throws IllegalStateException if the Resource does not exist.
	 */
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(unmarshallerMap, "The UnmarshallerMap must not be null.");
		
		Map<String, Unmarshaller> m = new HashMap<String, Unmarshaller>();
		
		for ( String root: unmarshallerMap.keySet() ) {
			String fragmentRootElementName = root;
			String fragmentRootElementNameSpace = null;
			if (fragmentRootElementName.contains("{")) {
				fragmentRootElementNameSpace = fragmentRootElementName.replaceAll("\\{(.*)\\}.*", "$1");
				fragmentRootElementName = fragmentRootElementName.replaceAll("\\{.*\\}(.*)", "$1");
			}
			
			m.put( fragmentRootElementName, unmarshallerMap.get( root ) );
			if ( fragmentRootElementNameSpace != null ) {
				namespaceMap.put( fragmentRootElementName, fragmentRootElementNameSpace);
			}			
		}
		
		unmarshallerMap = m;	
	}


	
	/**
	 * Responsible for moving the cursor before the StartElement of the fragment
	 * root.
	 * 
	 * This implementation simply looks for the next corresponding element, it
	 * does not care about element nesting. You will need to override this
	 * method to correctly handle composite fragments.
	 * 
	 * @return <code>true</code> if next fragment was found, <code>false</code>
	 * otherwise.
	 * 
	 * @throws NonTransientResourceException if the cursor could not be
	 * moved. This will be treated as fatal and subsequent calls to read will
	 * return null.
	 */
	protected boolean moveCursorToNextFragment(XMLEventReader reader) throws NonTransientResourceException {
		try {
			while (true) {
				while (reader.peek() != null && !reader.peek().isStartElement()) {
					reader.nextEvent();
				}
				if (reader.peek() == null) {
					return false;
				}
				QName startElementName = ((StartElement) reader.peek()).getName();
				
				Unmarshaller u = unmarshallerMap.get( startElementName.getLocalPart() );
				String namespace = namespaceMap.get( startElementName.getLocalPart() );
				
				if ( u != null ) {
					if (namespace == null || startElementName.getNamespaceURI().equals( namespace)  ) {
						unmarshaller = u;
						return true;
					}					
				}
				reader.nextEvent();
			}
		}
		catch (XMLStreamException e) {
			throw new NonTransientResourceException("Error while reading from event reader", e);
		}
	}

	protected void doClose() throws Exception {
		try {
			if (fragmentReader != null) {
				fragmentReader.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}
		finally {
			fragmentReader = null;
			inputStream = null;
		}

	}

	protected void doOpen() throws Exception {
		Assert.notNull(resource, "The Resource must not be null.");

		noInput = true;
		if (!resource.exists()) {
			if (strict) {
				throw new IllegalStateException("Input resource must exist (reader is in 'strict' mode)");
			}
			logger.warn("Input resource does not exist " + resource.getDescription());
			return;
		}
		if (!resource.isReadable()) {
			if (strict) {
				throw new IllegalStateException("Input resource must be readable (reader is in 'strict' mode)");
			}
			logger.warn("Input resource is not readable " + resource.getDescription());
			return;
		}

		inputStream = resource.getInputStream();
		eventReader = XMLInputFactory.newInstance().createXMLEventReader(inputStream);
		fragmentReader = new DefaultFragmentEventReader(eventReader);
		noInput = false;

	}

	/**
	 * Move to next fragment and map it to item.
	 */
	protected T doRead() throws Exception {

		if (noInput) {
			return null;
		}

		T item = null;

		boolean success = false;
		try {
			success = moveCursorToNextFragment(fragmentReader);
		}
		catch (NonTransientResourceException e) {
			// Prevent caller from retrying indefinitely since this is fatal
			noInput = true;
			throw e;
		}
		if (success) {
			fragmentReader.markStartFragment();

			@SuppressWarnings("unchecked")
			T mappedFragment = (T) unmarshaller.unmarshal(StaxUtils.getSource(fragmentReader));

			item = mappedFragment;
			fragmentReader.markFragmentProcessed();
		}

		return item;
	}

	/*
	 * jumpToItem is overridden because reading in and attempting to bind an
	 * entire fragment is unacceptable in a restart scenario, and may cause
	 * exceptions to be thrown that were already skipped in previous runs.
	 */
	@Override
	protected void jumpToItem(int itemIndex) throws Exception {
		for (int i = 0; i < itemIndex; i++) {
			readToStartFragment();
			readToEndFragment();
		}
	}

	/*
	 * Read until the first StartElement tag that matches the provided
	 * fragmentRootElementName. Because there may be any number of tags in
	 * between where the reader is now and the fragment start, this is done in a
	 * loop until the element type and name match.
	 */
	private void readToStartFragment() throws XMLStreamException {
		while (true) {
			XMLEvent nextEvent = eventReader.nextEvent();
			if (nextEvent.isStartElement()
					&& unmarshallerMap.containsKey( ((StartElement) nextEvent).getName().getLocalPart()) ) {
				return;
			}
		}
	}

	/*
	 * Read until the first EndElement tag that matches the provided
	 * fragmentRootElementName. Because there may be any number of tags in
	 * between where the reader is now and the fragment end tag, this is done in
	 * a loop until the element type and name match
	 */
	private void readToEndFragment() throws XMLStreamException {
		while (true) {
			XMLEvent nextEvent = eventReader.nextEvent();
			if (nextEvent.isEndElement()
					&& unmarshallerMap.containsKey( ((EndElement) nextEvent).getName().getLocalPart()) ) {
				return;
			}
		}
	}
}
