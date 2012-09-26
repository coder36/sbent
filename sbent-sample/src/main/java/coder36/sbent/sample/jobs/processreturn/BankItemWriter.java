package coder36.sbent.sample.jobs.processreturn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import coder36.sbent.sample.domain.Bank;
import coder36.sbent.sample.domain.stage.SReturn;
import coder36.sbent.hql.HibernateBatchUtils;

/**
 * Save new Bank's.  Check that Bank entity does not already exist before saving. 
 * @author Mark MIddleton
 */
public class BankItemWriter implements ItemWriter<Long[]> {

	@Autowired
	private SessionFactory sessionFactory;
	
	/**
	 * {@inheritDoc}
	 */
	public void write(List<? extends Long[]> ids) {
		Session session = sessionFactory.getCurrentSession();
		
		// use hibernate batch pattern
		Class [] classes = new Class [] { SReturn.class, Bank.class };
		List<Object []> objs = HibernateBatchUtils.read( session, ids, classes );
		
		List<String> names = new ArrayList<String>(ids.size());
		for ( Object [] o: objs ) {
			SReturn sret = (SReturn) o[0];
			names.add( sret.getBankName()); 
		}
		// look for customers which have already been saved.  Need to do this as 
		// ItemReader driving query will not know about any Bank objects which have already been
		// saved since the ItemReader query was run. 
		List<Bank> bs = session.createQuery( "select b from Bank b where b.name in (:names) " ).setParameterList( "names", names).list();						
		Map<String, Bank> banks = new HashMap<String,Bank>();
		for( Bank b: bs ) {
			banks.put( b.getName(), b);
		}
		
		for ( Object [] z: objs ) {
			SReturn ret = (SReturn) z[0];
			if( !banks.containsKey( ret.getBankName() ) ) {
				Bank b = new Bank();
				b.setName( ret.getBankName() );
				session.save(b);
				banks.put(b.getName(), b);
			}
						
			Bank b = banks.get( ret.getBankName());
			ret.setBank( b );							
			session.save( ret );
		}
	}
}
