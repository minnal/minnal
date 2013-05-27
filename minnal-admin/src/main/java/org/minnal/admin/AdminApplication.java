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
		resource(RouteResource.class).builder("/routes/{app_name}").action(HttpMethod.GET, "listRoutes");
		resource(ApplicationResource.class).builder("/applications").action(HttpMethod.GET, "listApplications");
	}

	@Override
	protected void defineResources() {
		addResource(RouteResource.class);
		addResource(ApplicationResource.class);
	}

}
