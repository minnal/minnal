/**
 * 
 */
package org.minnal.instrument.util;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.module.SimpleModule;

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
		super.setupModule(context);
	}
}
