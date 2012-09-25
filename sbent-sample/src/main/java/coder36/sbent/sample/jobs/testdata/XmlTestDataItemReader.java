package coder36.sbent.sample.jobs.testdata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.Marshaller;
import coder36.sbent.sample.schema.ret.ObjectFactory;
import coder36.sbent.sample.schema.ret.Return;
import coder36.sbent.xml.XmlUtils;

/**
 * Reader to generate a set realistic XML returns.
 * @author Mark
 *
 */
public class XmlTestDataItemReader implements ItemReader<String> {


	@Autowired( required=true )
	private Marshaller marshaller;

	// The number of customers per return
	@Value( "#{jobParameters['customerCount']?:1000}" )
	private int customerCount;

	// The number of transactions per customer
	@Value( "#{jobParameters['transactionCount']?:3}" )
	private int transactionCount;

	// The number of returns
	@Value( "#{jobParameters['returnCount']?:10}" )
	private int returnCount;

	// The number of errors
	@Value( "#{jobParameters['errorCount']?:0}" )
	private int errorCount;

	// The population size (a customer can belong to many banks)
	@Value( "#{jobParameters['populationSize']?:1000}" )
	private int populationSize;

	private String [] fnames;
	private String [] snames;
	private String [] banks;

	private int count = 0;
	private int bankCount = 0;
	private int rets = 0;

	/**
	 * Initialise test data, loading list of names and banks
	 */
	public XmlTestDataItemReader() {
		try {
			List<String> firstnames = new ArrayList<String>();
			List<String> surnames = new ArrayList<String>();
			List<String> names =  IOUtils.readLines( this.getClass().getResourceAsStream( "names.txt") );

			for ( String n: names ) {
				String [] x = StringUtils.split( n, " " );
				firstnames.add( x[0] );
				surnames.add( x[1] );
			}
			fnames = firstnames.toArray( new String[0] );
			snames = surnames.toArray( new String[0] );
			List<String> bs =  IOUtils.readLines( this.getClass().getResourceAsStream( "banks.txt") );
			banks = bs.toArray( new String[0] );
		}
		catch( IOException e ) {
			throw new RuntimeException( e );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		if ( rets++ == returnCount ) {
			return null;
		}

		if ( errorCount != 0 && rets % (returnCount/errorCount) == 0 ) {
			return "INJECTED ERROR";
		}

		// create XML test data
		ObjectFactory f = new ObjectFactory();
		Return r = f.createReturn();
		Return.Header hr = f.createReturnHeader();

		hr.setBankName( banks[bankCount++ % banks.length] );
		hr.setPeriodEnd( XmlUtils.toXml( new Date() ) );
		r.setHeader(hr);

		for( int i=0; i < customerCount; i++ ) {
			Return.Customer c = f.createReturnCustomer();
			Person p = getPerson( count ++ );
			c.setName( p.name );
			c.setNino( p.nino );

			for ( int j=0; j<transactionCount; j++) {
				Return.Customer.Transaction t = f.createReturnCustomerTransaction();
				t.setAmount( new BigDecimal( (count+101)  % 100 ) );
				c.getTransaction().add( t );
			}
			r.getCustomer().add( c );
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		marshaller.marshal( r, new StreamResult(bos) );
		bos.close();
		return bos.toString();
	}

	/**
	 * Model data set with at most 'populationSize' number people.  This means
	 * that if pos > populationSize, then data set will repeat
	 * @param pos
	 * @return Person
	 */
	private Person getPerson( int pos ) {
		int nsize = fnames.length;
		Person p = new Person();
		p.name = fnames[ (pos % populationSize ) % nsize ] + " " + snames[ ( (pos / nsize) % populationSize ) % nsize ];
		p.nino = String.format( "AB%06dA", pos % populationSize);
		return p;
	}

	private class Person {
		public String name;
		public String nino;
	}

}
