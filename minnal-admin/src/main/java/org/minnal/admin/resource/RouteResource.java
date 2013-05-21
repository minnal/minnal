/**
 * 
 */
package org.minnal.admin.resource;

import java.util.List;

import org.minnal.admin.ApplicationRoutes;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.route.Route;
import org.minnal.core.route.Routes;

/**
 * @author ganeshs
 *
 */
public class RouteResource {
	
	private static final String APP_NAME = "app_name";
	
	public List<Route> listRoutes(Request request, Response response) {
		String applicationName = request.getHeader(APP_NAME);
		Routes routes = ApplicationRoutes.instance.getRoutes(applicationName);
		return routes.getRoutes();
	}
}
