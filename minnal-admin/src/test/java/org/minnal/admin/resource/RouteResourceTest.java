/**
 * 
 */
package org.minnal.admin.resource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.testng.Assert.*;

import java.util.Arrays;

import org.minnal.admin.ApplicationRoutes;
import org.minnal.core.Application;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.route.Route;
import org.minnal.core.route.Routes;
import org.minnal.core.server.exception.NotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class RouteResourceTest {
	
	private Request request;
	
	private Response response;
	
	private RouteResource resource;
	
	@BeforeMethod
	public void setup() {
		request = mock(Request.class);
		response = mock(Response.class);
		resource = new RouteResource();
		Application<ApplicationConfiguration> application = mock(Application.class);
		ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
		when(configuration.getName()).thenReturn("admin");
		when(application.getConfiguration()).thenReturn(configuration);
		ResourceClass resource = mock(ResourceClass.class);
		when(application.getResources()).thenReturn(Arrays.asList(resource));
		Routes routes = mock(Routes.class);
		when(routes.getRoutes()).thenReturn(Arrays.asList(mock(Route.class)));
		when(application.getRoutes(resource)).thenReturn(routes);
		ApplicationRoutes.instance.addApplication(application);
	}

	@Test
	public void shouldListRoutes() {
		when(request.getHeader("app_name")).thenReturn("admin");
		assertEquals(resource.listRoutes(request, response).size(), 1);
	}
	
	@Test(expectedExceptions=NotFoundException.class)
	public void shouldThrowNotFoundIfApplicationIsNotFound() {
		when(request.getHeader("app_name")).thenReturn("unknown");
		resource.listRoutes(request, response);
	}
}
