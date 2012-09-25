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

import java.io.*;
import java.net.*;
import java.sql.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import coder36.sbent.resource.ProxyInputStream;
import coder36.sbent.resource.ResourceAdapter;

/**
 * Implementation of a spring io.Resource which binds to a database LOB,
 * handling the streaming, opening and closing of the underlying database connection.
 * Usage:
 * <bean id="lobResource" class="coder36.sbent.sql.LobResource">
 *   <property name="lobSql" value="select xml from xmldata "/>
 * </bean>
 * @author Mark Middleton
 */
public class LobResource extends ResourceAdapter {

	@Autowired
	private DataSource dataSource;

	private Connection conn;
	private ResultSet rs;
	private PreparedStatement ps;
	private Blob b;
	private String lobSql;

	/**
	 * @return InputStream A Lob inputStream
	 */
	public InputStream getInputStream() throws IOException {
		try {
			conn = dataSource.getConnection();
			ps =  conn.prepareStatement(lobSql);
			rs = ps.executeQuery();
			rs.next();
			b = rs.getBlob(1);
			return new LobInputStream( b.getBinaryStream() );
		}
		catch( SQLException e ) {
			throw new IOException( e );
		}
	}

	/**
	 * Set the SQL to drive the lob query
	 * @param sql
	 */
	public void setLobSql( String sql ) {
		this.lobSql = sql;
	}

	public boolean exists() {
		return true;
	}

	public boolean isReadable() {
		return true;
	}

	/**
	 * Implementation of InputStream which ensure database resources are closed
	 * correctly.
	 */
	public class LobInputStream extends ProxyInputStream {

		/**
		 * Constructor
		 * @param InputStream of LOB
		 */
		public LobInputStream( InputStream lobIs ) {
			super( lobIs );
		}

		/**
		 * Ensure database resources are closed
		 */
		public void close() throws IOException {
			try {
				if ( b != null ) b.free();
				if ( rs != null) rs.close();
				if (ps != null) ps.close();
				if ( conn != null ) conn.close();

				rs = null;
				ps = null;
				b = null;
				conn = null;
			}
			catch( SQLException e ) {
				throw new IOException( e );
			}
			super.close();
		}
	}
}
