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
package coder36.sbent.testing;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;

/**
 * Interface to Launch batch jobs from within a unit test.
 * @author Mark Middleton
 */
public interface JobLaunchingService {

	/**
	 * Launch a batch job, and block until complete.  If job has a
	 * JobParametersIncrementer, then parameters from previous job run
	 * will be passed.
	 *
	 * Will propogate first exception if ExitStatus==FAILED
	 * @param jobService
	 * @param job The job name
	 * @return JobExecution
	 */
	public JobExecution launchJob( String job );

	/**
	 * Launch a batch job, and block until complete.
	 * Propogate first exception if ExitStatus==FAILED
	 *
	 * @param jobService
	 * @param job The job name
	 * @param params Job parameters (as a , seperated list)
	 * @return JobExecution
	 */
	public  JobExecution launchJob( String job, String params );

	/**
	 * Launch a batch job, and block until complete.
	 * Propogate first exception if ExitStatus==FAILED
	 * @param jobService
	 * @param job
	 * @param params
	 * @return JobExecution
	 */
	public JobExecution launchJob( String job, JobParameters params );
}
