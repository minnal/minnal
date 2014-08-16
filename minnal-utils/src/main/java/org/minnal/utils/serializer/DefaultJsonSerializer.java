/**
 * 
 */
package org.minnal.utils.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author ganeshs
 *
 */
public class DefaultJsonSerializer extends AbstractJacksonSerializer {

	public DefaultJsonSerializer() {
		this(new ObjectMapper());
	}

	public DefaultJsonSerializer(ObjectMapper mapper) {
		super(mapper);
	}

	@Override
	protected void registerModules(ObjectMapper mapper) {
		mapper.registerModule(new SimpleModule());
	}
}
