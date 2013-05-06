/**
 * 
 */
package org.minnal.instrument;

import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.resource.ResourceClass;
import org.minnal.instrument.resource.ResourceEnhancer;

/**
 * @author ganeshs
 *
 */
public class ApplicationEnhancer {
	
	private Application<ApplicationConfiguration> application;
	
	public ApplicationEnhancer(Application<ApplicationConfiguration> application) {
		this.application = application;
	}
	
	public void enhance() {
		for (ResourceClass resource : application.getResources()) {
			ResourceEnhancer enhancer = new ResourceEnhancer(resource);
			enhancer.enhance();
		}
		
	}

}
