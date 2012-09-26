package coder36.sbdemo;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import coder36.sbent.sample.domain.Customer;
import coder36.sbent.sample.domain.Transaction;
import coder36.sbent.testing.JobLaunchingService;

@ContextConfiguration(locations = { "classpath*:/META-INF/spring/batch/bootstrap/**/*.xml", "classpath*:/META-INF/spring/batch/override/*.xml"  })
@RunWith(SpringJUnit4ClassRunner.class)
public class Coder36BlogTest {
	
	@Autowired
	ApplicationContext context;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private AmqpTemplate returnAmqpTemplate;	
	
	@Autowired
	private JobLaunchingService jobLauncher;
	
	/**
	 * Clear AMQP queue
	 */
	@Before
	public void clearQueue() {
		Object o;
		while ( ( o = returnAmqpTemplate.receiveAndConvert() ) != null ) {			
		}
	}
	
	@Before
	public void clearDatabase() {
		// clear down database using hibernate
		LocalSessionFactoryBean l = (LocalSessionFactoryBean) context.getBean( "&sessionFactory" );
		Configuration configuration = l.getConfiguration();
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
		configuration.buildSessionFactory(serviceRegistry);
	}
	
	@Test
	public void testHibernateBatchMode() {
		Session session = sessionFactory.openSession();
		
		String xml = 
				"<return>                                                                       " + 
				"  <header>                                                                     " + 
				"    <bankName>Error</bankName>                                                 " +
				"    <periodEnd>2012-10-10</periodEnd>                                          " +
				"  </header>                                                                    " +
				"  <customer>                                                                   " + 
				"    <name>Mark Middleton</name>                                               " + 
				"    <nino>AB123001A</nino>                                                     " +
				"    <transaction><amount>45.11</amount></transaction>                          " +
				"  </customer>                                                                  " +
				"  <customer>                                                                   " + 
				"    <name>John Smith</name>                                                    " + 
				"    <nino>AB123002A</nino>                                                     " +
				"    <transaction><amount>44.44</amount></transaction>                          " +
				"  </customer> 		                                                            " +
				"  <customer>                                                                   " + 
				"    <name>Fred Dibna</name>                                                    " + 
				"    <nino>AB123003A</nino>                                                     " +
				"    <transaction><amount>99.99</amount></transaction>                          " +
				"  </customer> 		                                                            " + 				
				"</return>                                                                      ";
		returnAmqpTemplate.convertAndSend( xml );		
		
		jobLauncher.launchJob( "loadFromQueue" );
		jobLauncher.launchJob( "loadReturn" );		
		jobLauncher.launchJob( "processReturns" );
		
		System.out.println( "getting list of Transaction's");
		List<Transaction> trans = session.createQuery( "select t from Transaction t" ).list();
		
		for ( Transaction t: trans ) {
			System.out.println( "getting customer");
			Customer c = t.getCustomer();
			System.out.println( "name: " + c.getName() );
		}		
	}
	
}
