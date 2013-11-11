/**
 * 
 */
package org.minnal.examples.petclinic;

import org.minnal.jpa.serializer.Hibernate4JsonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * @author ganeshs
 *
 */
public class JodatimeJsonSerializer extends Hibernate4JsonSerializer {

	@Override
	protected void registerModules(ObjectMapper mapper) {
		mapper.registerModule(new JodaModule());
		super.registerModules(mapper);
	}
}
