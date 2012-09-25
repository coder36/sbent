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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import org.springframework.core.io.Resource;

/**
 * A 'dummy' implementation of Resource.  Developers should extend and override methods
 * to provide a custom implementation
 * @author Mark Middleton
 */
public class ResourceAdapter implements Resource {

	public InputStream getInputStream() throws IOException { throw new RuntimeException( "Method Not Supported" ); }
	public boolean exists() { throw new RuntimeException( "Method Not Supported" ); }
	public boolean isReadable() { throw new RuntimeException( "Method Not Supported" ); }
	public boolean isOpen() { throw new RuntimeException( "Method Not Supported" ); }
	public URL getURL() { throw new RuntimeException( "Method Not Supported" ); }
	public URI getURI() { throw new RuntimeException( "Method Not Supported" ); }
	public File getFile() { throw new RuntimeException( "Method Not Supported" ); }
	public long contentLength() { throw new RuntimeException( "Method Not Supported" );}
	public long lastModified() { throw new RuntimeException( "Method Not Supported" ); }
	public Resource createRelative(String relativePath) { throw new RuntimeException( "Method Not Supported" ); }
	public String getFilename() { throw new RuntimeException( "Method Not Supported" ); }
	public String getDescription() { throw new RuntimeException( "Method Not Supported" ); }
}
