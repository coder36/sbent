package coder36.sbent.sample.jobs.loadbigreturn;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import coder36.sbent.sample.domain.stage.*;
import coder36.sbent.sample.schema.customer.Customer;
import coder36.sbent.sample.schema.header.*;
import coder36.sbent.xml.XmlUtils;

/**
 * ItemWriter to the loading of a large XML return.  The ItemReader will 
 * supply fragments of the large XML document.
 * 
 * The XML fragment must have its own xsd (as the xjc compiler will create
 * bindings for each fragment)
 * 
 * This allows large XML documents to be processed in a memory friendly way.
 * 
 * The downside is that only 1 document can be processed at a time.
 * @author Mark Middleton
 */
public class LoadBigReturnItemWriter implements ItemWriter<Object> {

	@Autowired
	private SessionFactory sessionFactory;
	
	private long id;		
	
	@BeforeStep
	public void beforeStep( StepExecution se ) {
		String id = se.getJobExecution().getExecutionContext().getString("id");
		if ( id != null ) this.id = new Long(id);
	}	
	
	/**
	 * {@inheritDoc}
	 */
	public void write(List<? extends Object> items) {
		
		Session session = sessionFactory.getCurrentSession();
		SReturn ret = (SReturn) session.get( SReturn.class, id );
		
		for ( Object o : items ) {
			
			// check if fragment is of type Headet
			if ( o instanceof Header ) {
				Header hdr = (Header)o;
				ret = new SReturn();
				ret.setBankName( hdr.getBankName() );
				ret.setPeriodEnd( XmlUtils.fromXml( hdr.getPeriodEnd() ) );
				session.save( ret );
			}
			
			// check if fragment is of type Customer
			if ( o instanceof Customer ) {
				Customer cus = (Customer) o;
				SCustomer scus = new SCustomer();
				scus.setName( cus.getName() );
				scus.setNino( cus.getNino() );
				for ( Customer.Transaction trans: cus.getTransaction() ) {
					STransaction strans = new STransaction();
					strans.setAmount( trans.getAmount() );
					scus.addTransaction( strans );
					scus.addTransaction( strans );
				}
				scus.setRet( ret );
				session.save( scus );
			}
		}
	}
}
