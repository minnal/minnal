/**
 * 
 */
package org.minnal.jpa.serializer;

import org.minnal.core.serializer.DefaultJsonSerializer;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module.Feature;

/**
 * @author ganeshs
 *
 */
public class Hibernate4JsonSerializer extends DefaultJsonSerializer {

	public Hibernate4JsonSerializer() {
		super(new Hibernate4Module());
	}

	@Override
	protected void init(Module module) {
		Hibernate4Module h4Module = (Hibernate4Module) module;
		h4Module.configure(Feature.FORCE_LAZY_LOADING, true);
	}
}
