/**
 * 
 */
package org.minnal.admin;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.Arrays;

import org.minnal.core.Application;
import org.minnal.core.Container;
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
public class AdminBundleTest {
	
	private Application<ApplicationConfiguration> application;
	
	@BeforeMethod
	public void setup() {
		application = mock(Application.class);
		ResourceClass resource = mock(ResourceClass.class);
		when(application.getResources()).thenReturn(Arrays.asList(resource));
		ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
		when(configuration.getName()).thenReturn("admin");
		when(application.getConfiguration()).thenReturn(configuration);
		Routes routes = mock(Routes.class);
		when(routes.getRoutes()).thenReturn(Arrays.asList(mock(Route.class)));
		when(application.getRoutes(resource)).thenReturn(routes);
	}
	
	@Test
	public void shouldRegisterListenerOnInit() {
		AdminBundle bundle = new AdminBundle();
		Container container = mock(Container.class);
		bundle.init(container);
		verify(container).registerListener(bundle);
	}
	
	@Test
	public void shouldAddApplicationPostMount() {
		AdminBundle bundle = new AdminBundle();
		bundle.postMount(application);
		assertNotNull(ApplicationRoutes.instance.getRoutes("admin"));
	}
	
	@Test(expectedExceptions=NotFoundException.class)
	public void shouldRemoveApplicationPostUnMount() {
		AdminBundle bundle = new AdminBundle();
		bundle.postMount(application);
		assertNotNull(ApplicationRoutes.instance.getRoutes("admin"));
		bundle.postUnMount(application);
		assertNull(ApplicationRoutes.instance.getRoutes("admin"));
	}

}
