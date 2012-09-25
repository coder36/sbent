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
package coder36.sbent.hql;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Joiner;

/**
 * Tasklet to lock a set of records based on a select and lock query.  The tasklet will
 * substitute the ids returned from the select query into the lock query.  The ids will
 * be put onto the ExecutionContext with a name of "ids".  The ids will be comma seperated
 * so can be substitued by subsequent steps.
 *
 *  Usage:
 * <bean id="lockRecords" class="coder36.sbdemo.batch.HqlLockRecordTasklet" scope="step">
 *   <property name="hqlSelectQuery" value="select id from SReturnXml where status='NEW'"/>
 *   <property name="hqlLockQuery" value="update SReturnXml set status='OPEN' where id in (:ids)"/>
 *   <property name="singleRow" value="false"/>
 * </bean>
 *
 * The Tasklet needs to be registered as listener on the step.
 *
 * @author Mark Middleton
 */
public class HqlLockRecordTasklet implements Tasklet {

	@Autowired
	private SessionFactory sessionFactory;

	private StepExecution se;

	private String hqlSelectQuery;
	private String hqlLockQuery;
	private boolean singleRow;
	private String paramName = "ids";

	@BeforeStep
	public void beforeStep( StepExecution se ) {
		this.se = se;
	}

	/**
	 * Locking query.  :ids will be replaced by selected ids
	 * eg.
	 * "update SReturnXml set status='OPEN' where id in (:ids)"
	 * @param hql
	 */
	@Required
	public void setHqlLockQuery( String hql ) {
		hqlLockQuery = hql;
	}

	/**
	 * Select query.  Query to generate a list of id's which will be substituted into the lockQuery.
	 * eg.
	 * "select id from SReturnXml where status='NEW'"
	 * @param hql
	 */
	@Required
	public void setHqlSelectQuery( String hql ) {
		hqlSelectQuery = hql;
	}

	/**
	 * IF set, then only first returned id will be used.  Default false
	 * @param singleRow
	 */
	public void setSingleRow( boolean singleRow ) {
		this.singleRow = singleRow;
	}

	/**
	 * Set the execution context parameter name
	 * @param name
	 */
	public void setParamName( String name ) {
		this.paramName = name;
	}

	/**
	 * {@inheritDoc}
	 */
	public RepeatStatus execute( StepContribution contribution, ChunkContext chunkContext) {
		Session session = sessionFactory.getCurrentSession();

		List<Long> ids = session.createQuery(hqlSelectQuery).list();
		if ( ! ids.isEmpty() ) {
			if( singleRow ) {
				Long id = ids.get(0);
				ids = new ArrayList<Long>();
				ids.add( id );
			}
			// put id's in the form id1,id2,id3,... onto execution context
			se.getJobExecution().getExecutionContext().putString(paramName, Joiner.on(",").join(ids) );
			session.createQuery( hqlLockQuery ).setParameterList("ids", ids).executeUpdate();
		}
		return RepeatStatus.FINISHED;
	}


}
