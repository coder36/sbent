package coder36.sbdemo;

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
import coder36.sbent.sample.domain.stage.SReturnXml;
import coder36.sbent.testing.JobLaunchingService;
import static junit.framework.Assert.*;
import static org.junit.Assert.assertEquals;


/**
 * Set of unit tests to prove ProcessReturn batch job
 * @author Mark Middleton
 */
@ContextConfiguration(locations = { "classpath*:/META-INF/spring/batch/bootstrap/**/*.xml", "classpath*:/META-INF/spring/batch/override/*.xml"  })
@RunWith(SpringJUnit4ClassRunner.class)
public class ReturnBatchTest {
		
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
	
	/**
	 * Test onSkip code is working
	 */
	@Test
	public void testOnError() {
		
		Session session = sessionFactory.openSession();
		
		// populate ampqQueue
		String xml = 
				"<return>                                                                       " + 
				"  <header>                                                                     " + 
				"    <bankName>Barclays Bank PLC</bankName>                                     " +
				"    <periodEnd>2012-10-10</periodEnd>                                          " +
				"  </header>                                                                    " +
				"  <customer>                                                                   " + 
				"    <name>Fred Jones</name>                                                    " + 
				"    <nino>AB123009A</nino>                                                     " +
				"    <transaction><amount>45.11</amount></transaction>                          " +
				"  </customer>                                                                  " + 
				"</return>                                                                      ";
		returnAmqpTemplate.convertAndSend( xml );	
		
		xml = 
				"<return>                                                                       " + 
				"  <header>                                                                     " + 
				"    <bankName>Error</bankName>                                                 " +
				"    <periodEnd>2012-10-10</periodEnd>                                          " +
				"  </header>                                                                    " +
				"  <customer>                                                                   " + 
				"    <ERROR>Mark Middleton</name>                                               " + 
				"    <nino>AB123001A</nino>                                                     " +
				"    <transaction><amount>45.11</amount></transaction>                          " +
				"  </customer>                                                                  " +
				"  <customer>                                                                   " + 
				"    <name>John Smith</name>                                                    " + 
				"    <nino>AB123002A</nino>                                                     " +
				"    <transaction><amount>44.44</amount></transaction>                          " +
				"  </customer> 		                                                            " + 
				"</return>                                                                      ";
		returnAmqpTemplate.convertAndSend( xml );		
		
		jobLauncher.launchJob( "loadFromQueue" );
		jobLauncher.launchJob( "loadReturn" );		
		jobLauncher.launchJob( "processReturns" );
		jobLauncher.launchJob( "report" );		
		
		// stdout will report skipped row
		assertEquals( new Long(1), session.createQuery( "select count(*) from Bank b" ).list().get(0) );
		assertEquals( new Long(1), session.createQuery( "select count(*) from Customer c" ).list().get(0) );
		assertEquals( new Long(1), session.createQuery( "select count(*) from Transaction t" ).list().get(0) );
		
	}
	
	/**
	 * Test loading data via AMQP Queue
	 */
	@Test
	public void testAmqpLoad() {
		
		Session session = sessionFactory.openSession();

		// populate ampqQueue
		String xml = 
				"<return>                                                                       " + 
				"  <header>                                                                     " + 
				"    <bankName>Barclays Bank PLC</bankName>                                     " +
				"    <periodEnd>2012-10-10</periodEnd>                                          " +
				"  </header>                                                                    " +
				"  <customer>                                                                   " + 
				"    <name>Mark Middleton</name>                                                " + 
				"    <nino>AB123001A</nino>                                                     " +
				"    <transaction><amount>45.11</amount></transaction>                          " +
				"  </customer>                                                                  " +
				"  <customer>                                                                   " + 
				"    <name>John Smith</name>                                                    " + 
				"    <nino>AB123002A</nino>                                                     " +
				"    <transaction><amount>44.44</amount></transaction>                          " +
				"  </customer> 		                                                            " + 
				"</return>                                                                      ";
		returnAmqpTemplate.convertAndSend( xml );	
		jobLauncher.launchJob( "loadFromQueue" );	
		SReturnXml s = (SReturnXml) session.createQuery( "select s from SReturnXml s" ).list().get(0);
		assertEquals( xml, s.getXml() );		
	}	
	
	/**
	 * Test batch jobs can handle large volumes of data
	 */
	@Test
	public void endToEndVolumeTest() {
		
		Session session = sessionFactory.openSession();
		jobLauncher.launchJob( "createQueueTestData", "customerCount=2000, transactionCount=2, returnCount=10, populationSize=500" );
		jobLauncher.launchJob( "loadFromQueue" );
		jobLauncher.launchJob( "loadReturn" );		
		jobLauncher.launchJob( "processReturns" );
		jobLauncher.launchJob( "report" );
		
		assertEquals( new Long(500), session.createQuery( "select count(*) from Customer c" ).list().get(0) );
		assertEquals( new Long(10), session.createQuery( "select count(*) from Bank b" ).list().get(0) );
		assertEquals( new Long(0), session.createQuery( "select count(*) from SReturn" ).list().get(0) );
		assertEquals( new Long(0), session.createQuery( "select count(*) from STransaction" ).list().get(0) );
		assertEquals( new Long(0), session.createQuery( "select count(*) from SCustomer" ).list().get(0) );
	}
	
	/**
	 * Integration test to prove processReturn set of batch jobs
	 * @throws Exception
	 */
	@Test
	public void endToEndTest() throws Exception {
		
		Session session = sessionFactory.openSession();
		
		String xml = 
		"<return>                                                                       " + 
		"  <header>                                                                     " + 
		"    <bankName>Barclays Bank PLC</bankName>                                     " +
		"    <periodEnd>2012-10-10</periodEnd>                                          " +
		"  </header>                                                                    " +
		"  <customer>                                                                   " + 
		"    <name>Mark Middleton</name>                                                " + 
		"    <nino>AB123001A</nino>                                                     " +
		"    <transaction><amount>45.11</amount></transaction>                          " +
		"  </customer>                                                                  " +
		"  <customer>                                                                   " + 
		"    <name>John Smith</name>                                                    " + 
		"    <nino>AB123002A</nino>                                                     " +
		"    <transaction><amount>44.44</amount></transaction>                          " +
		"  </customer> 		                                                            " + 
		"</return>                                                                      "; 
		loadTestData( xml );
		jobLauncher.launchJob( "loadBigReturn" );		
		jobLauncher.launchJob( "processReturns" );
		jobLauncher.launchJob( "report" );
		
		
		xml = 
		"<return>                                                                       " + 
		"  <header>                                                                     " + 
		"    <bankName>Barclays Bank PLC</bankName>                                     " +
		"    <periodEnd>2012-10-12</periodEnd>                                          " +
		"  </header>                                                                    " +
		"  <customer>                                                                   " + 
		"    <name>Mark Middleton</name>                                                " + 
		"    <nino>AB123001A</nino>                                                     " +
		"    <transaction><amount>66.11</amount></transaction>                          " +
		"    <transaction><amount>33.33</amount></transaction>                          " +
		"  </customer>                                                                  " + 		 
		"</return>                                                                      "; 		
		
		loadTestData( xml );
		jobLauncher.launchJob( "loadBigReturn" );
		jobLauncher.launchJob( "processReturns" );
		
		assertEquals( 1, session.createQuery( "select b from Bank b" ).list().size() );
		assertEquals( 1, session.createQuery( "select c from Customer c where c.nino='AB123001A'" ).list().size() );
		assertEquals( 1, session.createQuery( "select c from Customer c where c.nino='AB123002A'" ).list().size() );
		
		assertEquals( 3, session.createQuery( "select t from Transaction t, Customer c where c.nino='AB123001A' and t.customer = c" ).list().size() );
		assertEquals( 1, session.createQuery( "select t from Transaction t, Customer c where c.nino='AB123002A' and t.customer = c" ).list().size() );
		
	}
	
	/**
	 * Inject XML data into batch jobs
	 * @param xml
	 */
	private void loadTestData( String xml ) {
		Session session = sessionFactory.openSession();
		SReturnXml s = new SReturnXml();
		s.setXml( xml );
		session.save(s);
		session.flush();
	}
}
