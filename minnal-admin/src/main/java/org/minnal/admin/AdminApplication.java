/**
 * 
 */
package org.minnal.admin;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.admin.resource.ApplicationResource;
import org.minnal.admin.resource.RouteResource;
import org.minnal.core.Application;

/**
 * @author ganeshs
 *
 */
public class AdminApplication extends Application<AdminConfiguration> {

	@Override
	protected void defineRoutes() {
		resource(RouteResource.class).builder("/{app_name}").action(HttpMethod.GET, "listRoutes");
		resource(ApplicationResource.class).builder("/").action(HttpMethod.GET, "listApplications");
	}

	@Override
	protected void defineResources() {
		addResource(RouteResource.class, "/routes");
		addResource(ApplicationResource.class, "/applications");
	}
	
	@Override
	protected void addFilters() {
	}
	
	@Override
	protected void registerPlugins() {
	}

}
