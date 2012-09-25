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
package coder36.sbent.xml;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * XML parsing utilities
 * @author Mark Middleton
 */
public class XmlUtils {
	/**
	 * Convert date to a form which is compatible with jaxb
	 * @param d date
	 * @return XML version of date
	 */
	public static XMLGregorianCalendar toXml( Date d ) {
		try {
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(d);

			DatatypeFactory df = DatatypeFactory.newInstance();
			return df.newXMLGregorianCalendar(gc);
		}
		catch( Exception e ) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Convert from XMLGregorianCalendar to date
	 * @param xml
	 * @return Date
	 */
	public static Date fromXml( XMLGregorianCalendar xml ) {
		return xml.toGregorianCalendar().getTime();
	}
}
