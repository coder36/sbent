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

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

/**
 * ItemReader which reads from an AMQP Queue
 * @author Mark Middleton (inspiration from Chris Schaefer)
 * See https://github.com/SpringSource/spring-batch/commit/160701488650eead3da9997e5f84fec242b47dc4
 */
public class SimpleAmqpItemReader<T> implements ItemReader<T> {

	private AmqpTemplate amqpTemplate;

	public T read() {
		Object o = amqpTemplate.receiveAndConvert();
		return (T) o;
	}

	/**
	 * Set the AmqpTemplate.  Ensure that default queue is set
	 * @param amqpTemplate
	 */
	@Required
	public void setAmqpTemplate( AmqpTemplate amqpTemplate ) {
		this.amqpTemplate = amqpTemplate;
	}
}
