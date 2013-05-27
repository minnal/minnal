/**
 * 
 */
package org.minnal.admin.resource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import org.minnal.admin.ApplicationRoutes;
import org.minnal.core.Application;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.route.Route;
import org.minnal.core.route.Routes;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ApplicationResourceTest {

private Request request;
	
	private Response response;
	
	private ApplicationResource resource;
	
	@BeforeMethod
	public void setup() {
		request = mock(Request.class);
		response = mock(Response.class);
		resource = new ApplicationResource();
		Application<ApplicationConfiguration> application = mock(Application.class);
		ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
		when(configuration.getName()).thenReturn("admin");
		when(application.getConfiguration()).thenReturn(configuration);
		Routes routes = mock(Routes.class);
		when(routes.getRoutes()).thenReturn(Arrays.asList(mock(Route.class)));
		when(application.getRoutes()).thenReturn(routes);
		ApplicationRoutes.instance.addApplication(application);
	}

	@Test
	public void shouldListApplications() {
		assertEquals(resource.listApplications(request, response).size(), 1);
	}
}
