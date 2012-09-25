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

import java.util.Date;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * Used to record meta data about a batch event (usually an error)
 * @author Mark Middleton
 */
public class BatchEvent {

	public String eventType;
	public String processed;
	public String metadata;
	public String jobname;
	public String stepname;
	public String jobParameters;
	public String message;
	public String stackTrace;
	public String item;
	public Date time;


	/**
	 * Uses reflection to create a toString
	 */
	public String toString() {
		String date = DateFormatUtils.format(time, "dd/MM/yy : HH:mm:ss");

		String t = "";
		t += "@Type:       " + eventType.toString() + "\n";
		t += "@Timestamp:  " + date + "\n";
		t += "@Job:        " + jobname + "\n";
		t += "@Step:       " + stepname + "\n";
		t += "@JobParams:  " + jobParameters + "\n";
		t += "@Metadata:   " + metadata + "\n";
		t += "@Item:       " + item + "\n";
		t += "@Message:    " + message + "\n";
		t += "@StackTrace: " + "\n";
		t += stackTrace;
		t += "\n";
		t +=
				"========================================================================";

		return t;
	}
}

