/**
 * 
 */
package org.minnal.core.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * @author ganeshs
 *
 */
public class DefaultXmlSerializer extends AbstractJacksonSerializer {

	public DefaultXmlSerializer() {
		this(new XmlMapper());
	}

	public DefaultXmlSerializer(XmlMapper mapper) {
		super(mapper);
	}

	@Override
	protected void registerModules(ObjectMapper mapper) {
		JacksonXmlModule module = new JacksonXmlModule();
		module.setDefaultUseWrapper(false);
		mapper.registerModule(module);
	}
}
