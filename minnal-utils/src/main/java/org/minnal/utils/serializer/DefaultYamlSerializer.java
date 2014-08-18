/**
 * 
 */
package org.minnal.utils.serializer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

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
	
	@Override
	protected void init() {
		getMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		getMapper().setSerializationInclusion(Include.NON_EMPTY);
		getMapper().setSerializationInclusion(Include.NON_NULL);
		registerModules(getMapper());
	}
	
	protected void registerModules(ObjectMapper mapper) {
	}
}
