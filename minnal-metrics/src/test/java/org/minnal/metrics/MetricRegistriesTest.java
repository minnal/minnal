/**
 * 
 */
package org.minnal.metrics;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.codahale.metrics.MetricRegistry;

/**
 * @author ganeshs
 *
 */
public class MetricRegistriesTest {
	
	private Application application;
	
	private MetricRegistry metricRegistry;
	
	@BeforeMethod
	public void setup() {
		application = mock(Application.class);
		ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
		when(configuration.getName()).thenReturn("testname");
		when(application.getConfiguration()).thenReturn(configuration);
		metricRegistry = mock(MetricRegistry.class);
	}
	
	@AfterMethod
	public void destroy() {
		MetricRegistries.removeRegistry(application);
	}
	
	@Test
	public void shouldAddRegistry() {
		MetricRegistries.addRegistry(application, metricRegistry);
		assertEquals(MetricRegistries.getRegistry("testname"), metricRegistry);
	}
	
	@Test
	public void shouldRemoveRegistry() {
		MetricRegistries.removeRegistry(application);
		assertNull(MetricRegistries.getRegistry("testname"));
	}
}
