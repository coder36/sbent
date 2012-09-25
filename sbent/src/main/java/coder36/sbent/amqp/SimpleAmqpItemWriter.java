/*
 * Copyright 2012 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package coder36.sbent.amqp;

import java.util.List;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

/**
 * ItemWriter which writes items to an AMQP queue
 * @author Mark Middleton
 */
public class SimpleAmqpItemWriter<T> implements ItemWriter<T> {


	private AmqpTemplate amqpTemplate;

	public void write( List<? extends T> items ) {
		for( T t : items ) {
			amqpTemplate.convertAndSend(t);
		}
	}

	/**
	 * Set the AmqpTemplate.  Ensure that default routing key is used
	 * @param amqpTemplate
	 */
	@Required
	public void setAmqpTemplate( AmqpTemplate amqpTemplate ) {
		this.amqpTemplate = amqpTemplate;
	}


}
