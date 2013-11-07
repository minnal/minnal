/**
 * 
 */
package org.minnal.core.serializer;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * @author ganeshs
 *
 */
public class DefaultXmlSerializer extends AbstractJacksonSerializer {

	public DefaultXmlSerializer() {
		this(getDefaultModule());
	}

	public DefaultXmlSerializer(XmlMapper mapper) {
		this(mapper, getDefaultModule());
	}

	public DefaultXmlSerializer(Module module) {
		this(new XmlMapper(), module);
	}

	protected DefaultXmlSerializer(XmlMapper mapper, Module module) {
		super(mapper, module);
	}
	
	protected static Module getDefaultModule() {
		JacksonXmlModule module = new JacksonXmlModule();
		module.setDefaultUseWrapper(false);
		return module;
	}
}
