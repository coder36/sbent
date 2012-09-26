package coder36.sbent.sample.jobs.processreturn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import coder36.sbent.sample.domain.stage.*;
import coder36.sbent.sample.domain.*;
import coder36.sbent.hql.HibernateBatchUtils;

/**
 * Save new Customers.  Need to check that they don't alredy exist before saving
 * @author Mark Middleton
 */
public class CustomerItemWriter implements ItemWriter<Long[]> {

	@Autowired
	private SessionFactory sessionFactory;
	
	public void write( List< ? extends Long[] > ids ) {
		//sret.id, scus.id
		Session session = sessionFactory.getCurrentSession();

		// use hibernate batch pattern
		Class [] classes = new Class [] { SReturn.class, SCustomer.class, Bank.class, Customer.class };
		List<Object []> objs = HibernateBatchUtils.read( session, ids, classes );


		List<String> ninos = new ArrayList<String>();
		for ( Object [] o: objs ) {
			SCustomer scus = (SCustomer) o[1];
			ninos.add( scus.getNino() );			
		}
		// look for customers which have already been saved since ItemReader drving query was ran		
		List<Customer> customers = session.createQuery( "select c from Customer c where c.nino in (:ninos) " ).setParameterList( "ninos", ninos).list();		
		
		// create HashMap
		Map<String,Customer> m = new HashMap<String,Customer>();
		for( Customer c: customers ) {
			m.put( c.getNino(), c );
		}		
				
		for ( Object [] o : objs ) {			
			SCustomer scus = (SCustomer) o[1];
			Bank bank = (Bank) o[2];
			Customer cus = (Customer) o[3];
			
			// add to cache if need be
			if ( cus != null ) m.put( scus.getNino(), cus );			
			if ( cus == null && ! m.containsKey( scus.getNino()) )  m.put( scus.getNino(), new Customer() );			
			
			cus = m.get( scus.getNino() );			 
			cus.setName( scus.getName() );
			cus.setNino( scus.getNino() );				
			cus.getBanks().add( bank );
			session.save( cus );			
		}
	
	}
	
	
}
