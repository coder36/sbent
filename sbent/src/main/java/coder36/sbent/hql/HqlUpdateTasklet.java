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
 */
package coder36.sbent.hql;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

/**
 * Tasklet to perform a bulk update (provided as a HQL query)
 * <bean class="coder36.sbent.hql.HqlUpdateTasklet"
 *   <property name="hql" value="update Customer c set c.status='CLOSED'"/>
 * </bean>
 *
 * @author Mark Middleton
 */
public class HqlUpdateTasklet implements Tasklet {

	@Autowired
	private SessionFactory sessionFactory;

	private String hql;

	/**
	 * Set the HQL
	 * @param hql
	 */
	@Required
	public void setHql( String hql ) {
		this.hql = hql;
	}

	/**
	 * {@inheritDoc}
	 */
	public RepeatStatus execute( StepContribution contribution, ChunkContext chunkContext) {
		Session session = sessionFactory.getCurrentSession();
		session.createQuery( hql ).executeUpdate();
		return RepeatStatus.FINISHED;
	}
}
