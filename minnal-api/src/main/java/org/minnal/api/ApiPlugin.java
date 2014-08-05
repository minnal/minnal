/**
 * 
 */
package org.minnal.api;

import org.minnal.api.filter.CorsFilter;
import org.minnal.core.Application;
import org.minnal.core.Plugin;
import org.minnal.core.config.ApplicationConfiguration;

import com.wordnik.swagger.jaxrs.listing.ApiDeclarationProvider;
import com.wordnik.swagger.jaxrs.listing.ApiListingResourceJSON;
import com.wordnik.swagger.jaxrs.listing.ResourceListingProvider;

/**
 * @author ganeshs
 * 
 */
public class ApiPlugin implements Plugin {
	
	private boolean enableCors;
	
	/**
	 * @param enableCors
	 */
	public ApiPlugin(boolean enableCors) {
		this.enableCors = enableCors;
	}
	
	@Override
	public void init(Application<? extends ApplicationConfiguration> application) {
		application.addProvider(new ApiDeclarationProvider());
		application.addProvider(new ResourceListingProvider());
		application.addResource(ApiListingResourceJSON.class);
		if (enableCors) {
			application.addFilter(CorsFilter.class);
		}
	}
	
	@Override
	public void destroy() {
		
	}
}
