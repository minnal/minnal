/**
 * 
 */
package org.minnal.jpa.serializer;

import org.minnal.core.serializer.DefaultJsonSerializer;

import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

/**
 * @author ganeshs
 *
 */
public class Hibernate4JsonSerializer extends DefaultJsonSerializer {

	public Hibernate4JsonSerializer() {
		super(new Hibernate4Module());
	}
}
