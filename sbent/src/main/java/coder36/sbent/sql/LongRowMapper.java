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
 * Map result set onto an array of Longs.  Assumes all columns are of type
 * java.lang.long
 * @author Mark Middleton
 */
public class LongRowMapper implements RowMapper<Long[]> {

	public Long[] mapRow( ResultSet rs, int rowNum ) {
		try {
			List<Long> l = new ArrayList<Long>();
			for ( int i = 1; i <= rs.getMetaData().getColumnCount(); i++  ) {
				l.add( rs.getLong(i) );
			}
			return l.toArray( new Long[0] );
		}
		catch( SQLException s ) {
			throw new RuntimeException(s);
		}
	}
}
