/**
 * 
 */
package org.minnal.core.serializer;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @author ganeshs
 *
 */
public class DefaultJsonSerializer extends AbstractJacksonSerializer {

	public DefaultJsonSerializer() {
		this(new SimpleModule());
	}

	public DefaultJsonSerializer(ObjectMapper mapper) {
		this(mapper, new SimpleModule());
	}

	public DefaultJsonSerializer(Module module) {
		this(new ObjectMapper(), module);
	}

	protected DefaultJsonSerializer(ObjectMapper mapper, Module module) {
		super(mapper, module);
	}
}
