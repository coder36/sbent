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

import java.util.List;

import org.springframework.batch.admin.service.JobService;
import org.springframework.batch.admin.web.JobParametersExtractor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;

import coder36.sbent.logging.JsonBatchLoggingService;

/**
 * Implementation of JobLaunchingService
 * @author Mark Middleton
 */
public class SBEntJobLaunchingService implements JobLaunchingService {

	@Autowired
	private JsonBatchLoggingService log;

	@Autowired
	private JobService jobService;

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
	public JobExecution launchJob( String job ) {
		try {
			JobParameters p = new JobParameters();
			if( jobService.isIncrementable(job) ) {
				p = jobService.getLastJobParameters( job );
			}
			return launchJob( job, p );
		}
		catch( NoSuchJobException e ) {
			throw new RuntimeException( e );
		}
	}

	/**
	 * Launch a batch job, and block until complete.
	 * Propogate first exception if ExitStatus==FAILED
	 *
	 * @param jobService
	 * @param job The job name
	 * @param params Job parameters (as a , seperated list)
	 * @return JobExecution
	 */
	public  JobExecution launchJob( String job, String params ) {
		JobParametersExtractor x = new JobParametersExtractor();
		return launchJob( job, params==null ? new JobParameters() : x.fromString( params ) );
	}

	/**
	 * Launch a batch job, and block until complete.
	 * Propogate first exception if ExitStatus==FAILED
	 * @param jobService
	 * @param job
	 * @param params
	 * @return JobExecution
	 */
	public JobExecution launchJob( String job, JobParameters params ) {
		try {
			JobExecution je = jobService.launch( job, params );
			while ( je.isRunning() ) {
				Thread.sleep(1000);
				List<String> l = log.get( je.getJobId() );
				for( String s : l ) {
					System.out.println( s );
				}
			}
			if ( je.getExitStatus().equals(ExitStatus.FAILED) ) {
				throw new RuntimeException( "Job failed: " + job );
			}
			return je;
		}
		catch( Exception e ) {
			throw new RuntimeException( e );
		}
	}

}
