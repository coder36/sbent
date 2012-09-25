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


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Extension to BatchAdmin.  Maps the restful URL /logger/{jobId} onto the
 * JsonBatchLoggingService.
 *
 * @author Mark Middleton
 */
@Controller
public class BatchLoggerController {

	@Autowired
	JsonBatchLoggingService loggingService;

	@RequestMapping( value = "/logger/{jobId}", method = RequestMethod.GET)
	public String getLoggingEventsAsJson(ModelMap model, @ModelAttribute("jobId") Long id) {
		List<String> l = loggingService.get( id );
		String s = "";
		for ( String x: l ) {
			s += x + "\n";
		}
		model.addAttribute( "jobId", new Long(id) );
		model.addAttribute( "log", s );
		return "logger";
	}

}
