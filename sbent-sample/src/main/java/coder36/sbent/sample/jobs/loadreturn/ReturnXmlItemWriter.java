package coder36.sbent.sample.jobs.loadreturn;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import coder36.sbent.sample.domain.stage.SReturnXml;

/**
 * ItemWriter to save an xml String to a SReturnXml
 * @author Mark Middleton
 */
public class ReturnXmlItemWriter implements ItemWriter<String> {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	/**
	 * {@inheritDoc}
	 */
	public void write( List<? extends String> items  ) {
		
		Session session = sessionFactory.getCurrentSession();
		
		for( String xml : items ) {
			SReturnXml sret = new SReturnXml();
			sret.setXml( xml );
			session.save( sret );			
		}
	}
}
