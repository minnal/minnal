/**
 * 
 */
package org.minnal.core;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.ResourceConfiguration;
import org.minnal.core.config.RouteConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ApplicationContextTest {
	
	@BeforeMethod
	public void setup() {
		ApplicationContext.instance().clear();
	}

	@Test
	public void shouldSetApplicationConfigurationToContext() {
		ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
		ApplicationContext.instance().setApplicationConfiguration(configuration);
		assertEquals(ApplicationContext.instance().getApplicationConfiguration(), configuration);
	}
	
	@Test
	public void shouldSetResourceConfigurationToContext() {
		ResourceConfiguration configuration = mock(ResourceConfiguration.class);
		ApplicationContext.instance().setResourceConfiguration(configuration);
		assertEquals(ApplicationContext.instance().getResourceConfiguration(), configuration);
	}
	
	@Test
	public void shouldSetRouteConfigurationToContext() {
		RouteConfiguration configuration = mock(RouteConfiguration.class);
		ApplicationContext.instance().setRouteConfiguration(configuration);
		assertEquals(ApplicationContext.instance().getRouteConfiguration(), configuration);
	}
	
	@Test
	public void shouldClearContext() {
		RouteConfiguration configuration = mock(RouteConfiguration.class);
		ApplicationContext.instance().setRouteConfiguration(configuration);
		ApplicationContext.instance().clear();
		assertNull(ApplicationContext.instance().getRouteConfiguration());
	}
}
