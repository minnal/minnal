/**
 * 
 */
package org.minnal.utils.serializer;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
public class DefaultYamlSerializer extends AbstractJacksonSerializer {
	
	/**
	 * Default constructor
	 */
	public DefaultYamlSerializer() {
		this(new ObjectMapper(new YAMLFactory()));
	}
	
	public DefaultYamlSerializer(ObjectMapper mapper) {
		super(mapper);
	}
	
	private static Module createSimpleModule() {
		SimpleModule module = new SimpleModule();
		module.addKeySerializer(MediaType.class, new JsonSerializer<MediaType>() {
			@Override
			public void serialize(MediaType value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
				jgen.writeFieldName(String.valueOf(value.withoutParameters().toString()));
			}
		});;
		return module;
	}
	
	@Override
	protected void init() {
		getMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		getMapper().setSerializationInclusion(Include.NON_EMPTY);
		getMapper().setSerializationInclusion(Include.NON_NULL);
		registerModules(getMapper());
	}
	
	@Override
	protected void registerModules(ObjectMapper mapper) {
		mapper.registerModule(createSimpleModule());
	}
}
