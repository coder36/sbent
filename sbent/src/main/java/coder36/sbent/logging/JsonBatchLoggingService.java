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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;


/**
 * Implementation of Batch logging service which records logging events in memory.
 * Need to be caredull to peridically read the logs to clear events.
 * This can then be wired into a web service to provide an online mechanism to retrieve log messages.
 * @author Mark Middleton
 */
public class JsonBatchLoggingService implements BatchLoggingService {

	static private Map<Long,List<String> > logs = Collections.synchronizedMap( new HashMap<Long,List<String> >() );

	/**
	 * {@inheritDoc}
	 */
	public void log( Long jobId, String msg ) {
		List<String> l = logs.get( jobId );
		if ( l == null ) {
			l = new ArrayList<String>();
			logs.put( jobId, l);
		}
		l.add( msg);
	}

	/**
	 * Read the log
	 * @param id
	 * @return
	 */
	public List<String> get( Long jobId) {
		List<String> l = logs.get( jobId );
		if ( l == null ) {
			l = new ArrayList<String>();
		}
		else {
			logs.remove( jobId );
		}
		return l;
	}

}
