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
package coder36.sbent.logging;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.base.Joiner;


/**
 * Listener which writes all error events ie. SkippedRows to the batchLoggingService
 * @author Mark Middleton
 */
public class ErrorLoggerListener {

	@Autowired
	private BatchLoggingService loggingService;

	private Map<Long,StepExecution> stepExecutions = Collections.synchronizedMap( new HashMap<Long,StepExecution>() );

	private String jobName;
	private Long jobId;

	@BeforeStep
	void beforeStep(StepExecution stepExecution) {
		stepExecutions.put( Thread.currentThread().getId(), stepExecution );
		jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
		jobId = stepExecution.getJobExecution().getJobInstance().getId();
	}

	@OnSkipInWrite
	public void onSkipInWrite(Object item, Throwable t) {
		String i = (item instanceof Object [] ) ? Joiner.on(",").join( (Object []) item) :item.toString();
		BatchEvent e = reportSkippedRow( getStepExecution(), i, t );
		loggingService.log(jobId, e.toString() );
	}

	private StepExecution getStepExecution() {
		return stepExecutions.get( Thread.currentThread().getId() );
	}


	private BatchEvent reportSkippedRow(StepExecution stepExecution, Object item, Throwable t) {
		BatchEvent se = new BatchEvent();
		se.stackTrace = ExceptionUtils.getFullStackTrace(t);
		se.time = new Date();
		se.metadata = stepExecution.getJobExecution() + stepExecution.getSummary();
		se.item = item.toString();
		se.message = t.getMessage();
		se.jobname = stepExecution.getJobExecution().getJobInstance().getJobName();
		se.stepname = stepExecution.getStepName();
		se.jobParameters = getJobParameters(stepExecution.getJobParameters());
		se.eventType = "SkippedRow";
		return se;
	}

	/**
	 * Get the job parameters
	 *
	 * @param jobParams
	 * @return A list of job parameters
	 */
	private String getJobParameters(JobParameters jobParams) {
		String s = "";
		Map<String, JobParameter> m = jobParams.getParameters();
		for (String key : m.keySet()) {
			s += key + "=" + m.get(key).toString() + ",";
		}
		return s;
	}
}
