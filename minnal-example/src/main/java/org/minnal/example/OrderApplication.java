/**
 * 
 */
package org.minnal.example;

import org.minnal.core.Application;
import org.minnal.core.MinnalException;
import org.minnal.core.server.exception.InternalServerErrorException;
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
	}

	@Override
	protected void defineResources() {
	}
	
	@Override
	protected void addFilters() {
	}
	
	@Override
	protected void registerPlugins() {
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
