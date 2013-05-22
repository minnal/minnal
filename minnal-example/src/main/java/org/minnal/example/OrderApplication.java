/**
 * 
 */
package org.minnal.example;

import org.minnal.core.Application;
import org.minnal.core.MinnalException;
import org.minnal.core.server.exception.InternalServerErrorException;
import org.minnal.example.resource.OrderResource;
import org.minnal.jpa.JPAPlugin;

/**
 * @author ganeshs
 *
 */
public class OrderApplication extends Application<OrderConfiguration> {
	
	public OrderApplication() {
		registerPlugin(new JPAPlugin());
	}

	@Override
	protected void defineRoutes() {
//		resource(OrderResource.class).builder("/hello").action(HttpMethod.GET, "hello");
	}

	@Override
	protected void defineResources() {
//		addResource(OrderResource.class);
	}
	
	@Override
	protected void mapExceptions() {
		addExceptionMapping(MinnalException.class, InternalServerErrorException.class);
		super.mapExceptions();
	}
	
	@Override
	public boolean shouldInstrument() {
		return true;
	}

}
