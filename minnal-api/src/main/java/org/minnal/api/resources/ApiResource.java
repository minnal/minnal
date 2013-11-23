/**
 * 
 */
package org.minnal.api.resources;

import org.minnal.api.ApiDocumentation;
import org.minnal.core.Request;
import org.minnal.core.Response;

import com.wordnik.swagger.model.ApiListing;
import com.wordnik.swagger.model.ResourceListing;

/**
 * @author ganeshs
 *
 */
public class ApiResource {

	public ResourceListing listResources(Request request, Response response) {
		String applicationName = request.getHeader("application_name");
		return ApiDocumentation.instance.getResourceListing(applicationName);
	}
	
	public ApiListing listResourceApis(Request request, Response response) {
		String applicationName = request.getHeader("application_name");
		String resourceName = request.getHeader("resource_name");
		return ApiDocumentation.instance.getApiListing(applicationName, resourceName);
	}
}
