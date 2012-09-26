package coder36.sbent.sample.jobs.processreturn;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import coder36.sbent.hql.HibernateBatchUtils;
import coder36.sbent.sample.domain.Bank;
import coder36.sbent.sample.domain.Customer;
import coder36.sbent.sample.domain.Transaction;
import coder36.sbent.sample.domain.stage.STransaction;

/**
 * Save Transactions
 * @author Mark Middleton
 *
 */
public class TransactionItemWriter implements ItemWriter<Long[]> {

	@Autowired
	private SessionFactory sessionFactory;
	
	/**
	 * {@inheritDoc}
	 */
	public void write( List< ? extends Long[] > ids ) {
		//strans.id, cus.id, sret.bank_id
		Session session = sessionFactory.getCurrentSession();

		// use hibernate batch pattern
		Class [] classes = new Class [] { STransaction.class, Customer.class, Bank.class };
		List<Object []> objs = HibernateBatchUtils.read( session, ids, classes );
		
		for( Object [] o : objs ) {
			STransaction strans = (STransaction) o[0];
			Customer cus = (Customer) o[1];
			Bank bank = (Bank) o[2];
			
			Transaction trans = new Transaction();
			trans.setBank( bank );
			trans.setCustomer( cus );
			trans.setAmount( strans.getAmount() );	
			session.save( trans );
		}
		
	}
		
}
