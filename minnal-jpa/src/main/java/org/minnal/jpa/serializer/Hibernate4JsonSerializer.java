/**
 * 
 */
package org.minnal.jpa.serializer;

import org.minnal.core.serializer.DefaultJsonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module.Feature;

/**
 * @author ganeshs
 *
 */
public class Hibernate4JsonSerializer extends DefaultJsonSerializer {

	@Override
	protected void registerModules(ObjectMapper mapper) {
		Hibernate4Module module = new Hibernate4Module();
		module.configure(Feature.FORCE_LAZY_LOADING, true);
		mapper.registerModule(module);
	}
}
