/**
 * 
 */
package org.minnal.example;

import org.minnal.core.Application;
import org.minnal.core.MinnalException;
import org.minnal.core.server.exception.InternalServerErrorException;
import org.minnal.jpa.JPAPlugin;
import org.minnal.jpa.OpenSessionInViewFilter;

/**
 * @author ganeshs
 *
 */
public class OrderApplication extends Application<OrderConfiguration> {
	
	public OrderApplication() {
	}

	@Override
	protected void defineRoutes() {
//		resource(OrderResource.class).builder("/hello").action(HttpMethod.GET, "helloWorld");
	}

	@Override
	protected void defineResources() {
//		addResource(OrderResource.class);
	}
	
	@Override
	protected void addFilters() {
		addFilter(new OpenSessionInViewFilter(getConfiguration().getDatabaseConfiguration()));
	}
	
	@Override
	protected void registerPlugins() {
		registerPlugin(new JPAPlugin());
	}
	
	@Override
	protected void mapExceptions() {
		addExceptionMapping(MinnalException.class, InternalServerErrorException.class);
		super.mapExceptions();
	}
}
