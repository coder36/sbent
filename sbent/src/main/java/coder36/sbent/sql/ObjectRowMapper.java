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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

/**
 * Map result set onto an array of Objects.  Makes no assumptions about columnn
 * type
 * @author Mark Middleton
 */
public class ObjectRowMapper<T> implements RowMapper<T[]> {

	/**
	 * {@inheritDoc}
	 */
	public T[] mapRow( ResultSet rs, int rowNum ) {
		try {
			List<T> l = new ArrayList<T>();
			for ( int i = 1; i <= rs.getMetaData().getColumnCount(); i++  ) {
				l.add( (T) rs.getObject( i ) );
			}
			return l.toArray( (T[]) new Object[0] );
		}
		catch( SQLException s ) {
			throw new RuntimeException(s);
		}
	}
}
