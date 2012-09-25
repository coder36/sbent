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
package coder36.sbent.flow;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.beans.factory.annotation.Required;

/**
 * Simple decider which checks for the presence of a parameter on the
 * execution context.  If param not found decider returns FAILED.
 * @author Mark Middleton
 *
 */
public class ExecutionContextParamDecider implements JobExecutionDecider {

	private String paramName;

	/**
	 * Set the parameter
	 * @param param
	 */
	@Required
	public void setParamName( String paramName ) {
		this.paramName = paramName;
	}

	/**
	 * Implementation of JobExecutionDecider, which looks at whether data was
	 * returned.
	 */
	public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
		if ( ! jobExecution.getExecutionContext().containsKey( paramName ) ) {
			return FlowExecutionStatus.FAILED;
		}
		else {
			return FlowExecutionStatus.COMPLETED;
		}
	}
}
