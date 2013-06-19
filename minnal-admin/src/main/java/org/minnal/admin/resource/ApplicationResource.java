/**
 * 
 */
package org.minnal.admin.resource;

import java.util.ArrayList;
import java.util.List;

import org.minnal.admin.ApplicationRoutes;
import org.minnal.admin.model.ApplicationData;
import org.minnal.core.Application;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.config.ApplicationConfiguration;

/**
 * @author ganeshs
 *
 */
public class ApplicationResource {

	public List<ApplicationData> listApplications(Request request, Response response) {
		List<ApplicationData> data = new ArrayList<ApplicationData>();
		for (Application<ApplicationConfiguration> application : ApplicationRoutes.instance.getApplications()) {
			data.add(new ApplicationData(application));
		}
		return data;
	}
}
