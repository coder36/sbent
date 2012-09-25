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
package coder36.sbent.sql;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Required;

/**
 * Partitioner which is configured with a template in the form of a string.
 * The template can contain the following tokens:
 * #partid#  - will be replaced with the partition id (0 - gridSize-1)
 * #gridSize# - will be replaced with the gridsize
 *
 * The completed template will be available via:
 * #{stepExecutionContext['partition']}
 *
 * Example.
 * <bean class="coder36.sent.sql.CountablePartitioner>
 *   <param property="text" value="mod(i, #gridSize#) = #partid#"/>
 * </bean>
 * with a gridSize of 4 would generate the following:
 * partition0 : partition =  mod(i, 4) = 0
 * partition1 : partition =  mod(i, 4) = 1
 * partition2 : partition =  mod(i, 4) = 2
 * partition3 : partition =  mod(i, 4) = 3
 *
 * @author Mark Middleton
 */
public class TemplatePartitioner implements Partitioner {

	private String template;

	/**
	 * {@inheritDoc}
	 */
	public Map<String, ExecutionContext> partition(int gridSize) {

		Map<String, ExecutionContext> map = new HashMap<String, ExecutionContext>(gridSize);
		for (int i = 0; i < gridSize; i++) {
			ExecutionContext context = new ExecutionContext();
			String s = template.replaceAll( "#partid#", Integer.toString(i));
			s = s.replaceAll( "#gridSize#", Integer.toString(gridSize));
			context.putString("partition", s );
			map.put("partition" + i, context);
		}
		return map;
	}

	/**
	 * Set the template
	 * #partid# will be substituted with the partition id)
	 * #gridSize# will be replaced with the gridSize
	 * @param sql
	 */
	@Required
	public void setTemplate( String template ) {
		this.template = template;
	}


}
