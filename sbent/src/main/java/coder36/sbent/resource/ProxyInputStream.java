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
package coder36.sbent.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of InputStream which delegates all calls to an underlying
 * InputStream.  The idea being, the specialized behaviour can be added by
 * overriding methods in a derrived class.
 * @author Mark Middleton
 */
public class ProxyInputStream extends InputStream  {

	protected InputStream is;

	/**
	 * Constructor
	 * @param InputStream the underlying input stream
	 */
	public ProxyInputStream( InputStream is ) {
		this.is = is;
	}

	public void close() throws IOException { is.close(); }
	public int available() throws IOException { return is.available(); }
	public void mark( int readlimit ) { is.mark(readlimit); }
	public boolean markSupported() { return is.markSupported(); }
	public int read() throws IOException { return is.read(); }
	public int read(byte [] b) throws IOException { return is.read(b); }
	public int read(byte[] b, int off, int len) throws IOException { return is.read( b, off, len ); }
	public void reset() throws IOException { is.reset(); }
	public long skip( long n ) throws IOException { return is.skip(n); }
}
