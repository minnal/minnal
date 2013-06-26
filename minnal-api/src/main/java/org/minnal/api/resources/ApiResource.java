/**
 * 
 */
package org.minnal.api.resources;

import org.minnal.api.ApiDocumentation;
import org.minnal.core.Request;
import org.minnal.core.Response;

import com.wordnik.swagger.core.Documentation;

/**
 * @author ganeshs
 *
 */
public class ApiResource {

	public Documentation listResources(Request request, Response response) {
		String applicationName = request.getHeader("application_name");
		return ApiDocumentation.instance.getDocumentation(applicationName);
	}
	
	public Documentation listResourceApis(Request request, Response response) {
		String applicationName = request.getHeader("application_name");
		String resourceName = request.getHeader("resource_name");
		return ApiDocumentation.instance.getDocumentation(applicationName, resourceName);
	}
}
