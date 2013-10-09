/**
 * 
 */
package org.minnal.core;

import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.ResourceConfiguration;
import org.minnal.core.config.RouteConfiguration;

/**
 * Application context specific to the current thread
 * 
 * @author ganeshs
 *
 */
public class ApplicationContext {
	
	private static final ThreadLocal<Context> context = new ThreadLocal<ApplicationContext.Context>();
	
	private static ApplicationContext applicationContext = new ApplicationContext();
	
	/**
	 * Single instance
	 * 
	 * @return
	 */
	public static ApplicationContext instance() {
		return applicationContext;
	}
	
	/**
	 * Returns the application configuration for the current request
	 * 
	 * @return
	 */
	public ApplicationConfiguration getApplicationConfiguration() {
		return getContext().getApplicationConfiguration();
	}
	
	/**
	 * Returns the resource configuration for the current request
	 * 
	 * @return
	 */
	public ResourceConfiguration getResourceConfiguration() {
		return getContext().getResourceConfiguration();
	}
	
	/**
	 * Returns the route configuration for the current request
	 * 
	 * @return
	 */
	public RouteConfiguration getRouteConfiguration() {
		return getContext().getRouteConfiguration();
	}
	
	/**
	 * Sets the application configuration for the current request
	 * 
	 * @param configuration
	 */
	void setApplicationConfiguration(ApplicationConfiguration configuration) {
		getContext().setApplicationConfiguration(configuration);
	}
	
	/**
	 * Sets the resource configuration for the current request
	 * 
	 * @param configuration
	 */
	void setResourceConfiguration(ResourceConfiguration configuration) {
		getContext().setResourceConfiguration(configuration);
	}
	
	/**
	 * Sets the route configuration for the current request
	 * 
	 * @param configuration
	 */
	void setRouteConfiguration(RouteConfiguration configuration) {
		getContext().setRouteConfiguration(configuration);
	}
	
	/**
	 * Returns the context
	 * 
	 * @return
	 */
	private Context getContext() {
		Context cxt = context.get();
		if (cxt == null) {
			cxt = new Context();
			context.set(cxt);
		}
		return cxt;
	}
	
	/**
	 * Clears the context;
	 */
	public void clear() {
		context.remove();
	}
	
	/**
	 * @author ganeshs
	 *
	 */
	private static class Context {
		
		private ApplicationConfiguration applicationConfiguration;
		
		private RouteConfiguration routeConfiguration;
		
		private ResourceConfiguration resourceConfiguration;

		/**
		 * @return the applicationConfiguration
		 */
		public ApplicationConfiguration getApplicationConfiguration() {
			return applicationConfiguration;
		}

		/**
		 * @param applicationConfiguration the applicationConfiguration to set
		 */
		public void setApplicationConfiguration(
				ApplicationConfiguration applicationConfiguration) {
			this.applicationConfiguration = applicationConfiguration;
		}

		/**
		 * @return the routeConfiguration
		 */
		public RouteConfiguration getRouteConfiguration() {
			return routeConfiguration;
		}

		/**
		 * @param routeConfiguration the routeConfiguration to set
		 */
		public void setRouteConfiguration(RouteConfiguration routeConfiguration) {
			this.routeConfiguration = routeConfiguration;
		}

		/**
		 * @return the resourceConfiguration
		 */
		public ResourceConfiguration getResourceConfiguration() {
			return resourceConfiguration;
		}

		/**
		 * @param resourceConfiguration the resourceConfiguration to set
		 */
		public void setResourceConfiguration(ResourceConfiguration resourceConfiguration) {
			this.resourceConfiguration = resourceConfiguration;
		}
	}
}
