/**
 * 
 */
package org.minnal.admin;

import java.util.HashMap;
import java.util.Map;

import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.route.Routes;
import org.minnal.core.server.exception.NotFoundException;

/**
 * @author ganeshs
 *
 */
public class ApplicationRoutes {

	private Map<String, Application<ApplicationConfiguration>> applications = new HashMap<String, Application<ApplicationConfiguration>>();
	
	public static final ApplicationRoutes instance = new ApplicationRoutes();
	
	private ApplicationRoutes() {}
	
	public void addApplication(Application<ApplicationConfiguration> application) {
		applications.put(application.getConfiguration().getName(), application);
	}
	
	public void removeApplication(Application<ApplicationConfiguration> application) {
		applications.remove(application.getConfiguration().getName());
	}
	
	public Routes getRoutes(String applicationName) {
		Application<ApplicationConfiguration> application = applications.get(applicationName);
		if (application == null) {
			throw new NotFoundException("Application " + applicationName + " is not found");
		}
		return application.getRoutes();
	}
}
