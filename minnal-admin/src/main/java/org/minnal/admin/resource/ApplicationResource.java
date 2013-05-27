/**
 * 
 */
package org.minnal.admin.resource;

import java.util.Collection;

import org.minnal.admin.ApplicationRoutes;
import org.minnal.core.Application;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.config.ApplicationConfiguration;

/**
 * @author ganeshs
 *
 */
public class ApplicationResource {

	public Collection<Application<ApplicationConfiguration>> listApplications(Request request, Response response) {
		return ApplicationRoutes.instance.getApplications();
	}
}
