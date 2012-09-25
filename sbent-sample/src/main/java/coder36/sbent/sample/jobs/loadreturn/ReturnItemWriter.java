package coder36.sbent.sample.jobs.loadreturn;

import java.io.StringReader;
import java.util.List;

import javax.xml.transform.stream.StreamSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;

import coder36.sbent.sample.domain.stage.*;
import coder36.sbent.sample.schema.ret.Return;

/**
 * ItemWriter to handle small XML returns.  Converts the
 * XML into staging entities and saves to database
 * The XML parsing is done entirely in memory, hence should only use 
 * for small XML.
 * @author Mark Middleton
 */
public class ReturnItemWriter implements ItemWriter<Long> {

	@Autowired
	private Unmarshaller unmarshaller;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public void write( List<? extends Long> ids ) throws Exception {
		Session session = sessionFactory.getCurrentSession();
		for( Long id : ids ) {
			SReturnXml sret = (SReturnXml) session.get( SReturnXml.class, id);
			Object o = unmarshaller.unmarshal( new StreamSource( new StringReader( sret.xml ) ) );
			Return r = (Return) o;
			sret.status = Status.OPEN;
			session.save( convert( r ) );			
			session.save( sret );
		}
	}
	
	/**
	 * Convert from jaxb to staging domain objects
	 * @param r
	 * @return
	 */
	private SReturn convert( Return r ) {
		
		SReturn sret = new SReturn();
		sret.bankName = r.getHeader().getBankName();
		
		for ( Return.Customer c : r.getCustomer() ) {
			SCustomer sc = new SCustomer();
			sc.name = c.getName();
			sc.nino = c.getNino();
			for( Return.Customer.Transaction t: c.getTransaction() ) {
				STransaction st = new STransaction();
				st.amount = t.getAmount();
				sc.addTransaction( st );
			}
			sret.addCustomer( sc );			
		}
		return sret;
	}
}
