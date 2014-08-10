/**
 * 
 */
package org.minnal.instrument.util;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * @author ganeshs
 *
 */
public class MinnalModule extends SimpleModule {

	private static final long serialVersionUID = 1L;

	@JsonFilter("property_filter")  
	public class PropertyFilterMixIn {}
	
	@Override
	public void setupModule(SetupContext context) {
		context.setMixInAnnotations(Object.class, PropertyFilterMixIn.class);
		((ObjectMapper) context.getOwner()).setFilters(new SimpleFilterProvider().addFilter("property_filter", SimpleBeanPropertyFilter.serializeAllExcept(new String[0])));
		super.setupModule(context);
	}
}
