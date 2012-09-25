package coder36.sbent.sample.jobs.report;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Required;

/**
 * Write to AMQP queue
 * @author Mark Middleton
 *
 */
public class TickerItemWriter implements ItemWriter<Object[]> {

	private AmqpTemplate amqpTemplate;

	/**
	 * {@inheritDoc}
	 */
	public void write(List<? extends Object[]> items) throws Exception {
		
		for( Object [] o : items ) {
			String nino = (String) o[0];
			BigDecimal amount = (BigDecimal) o[1];			
			String s = nino + "   : £" + amount;
			amqpTemplate.convertAndSend(s);
		}		
	}
	
	/**
	 * Set AMQPTemplate.  Ensure routingKey is set.
	 * @param amqpTemplate
	 */
	@Required
	public void setAmqpTemplate( AmqpTemplate amqpTemplate ) {
		this.amqpTemplate = amqpTemplate;
	}
}
