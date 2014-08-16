/**
 * 
 */
package org.minnal.instrument;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import org.glassfish.jersey.server.ResourceConfig;
import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.DatabaseConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class MinnalApplicationEnhancerTest {
	
	private Application<ApplicationConfiguration> application;
	
	private ResourceConfig resourceConfig;
	
	@BeforeMethod
	public void setup() {
		resourceConfig = mock(ResourceConfig.class);
		application = mock(Application.class);
		when(application.getResourceConfig()).thenReturn(resourceConfig);
		ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
		DatabaseConfiguration databaseConfiguration = mock(DatabaseConfiguration.class);
		when(databaseConfiguration.getPackagesToScan()).thenReturn(Lists.newArrayList("com.test", "com.test.app"));
		when(configuration.getDatabaseConfiguration()).thenReturn(databaseConfiguration);
		when(application.getConfiguration()).thenReturn(configuration);
	}

	@Test
	public void shouldGetPackagesToScan() {
		Application<ApplicationConfiguration> application = mock(Application.class);
		ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
		DatabaseConfiguration databaseConfiguration = mock(DatabaseConfiguration.class);
		when(databaseConfiguration.getPackagesToScan()).thenReturn(Lists.newArrayList("com.test", "com.test.app"));
		when(configuration.getDatabaseConfiguration()).thenReturn(databaseConfiguration);
		when(application.getConfiguration()).thenReturn(configuration);
		assertEquals(MinnalApplicationEnhancer.getPackagesToScan(application), new String[]{"com.test", "com.test.app"});
	}
	
	@Test
	public void shouldAddResource() {
		NamingStrategy strategy = mock(NamingStrategy.class);
		MinnalApplicationEnhancer enhancer = new MinnalApplicationEnhancer(application, strategy);
		enhancer.addResource(DummyResource.class);
		verify(resourceConfig).register(DummyResource.class);
	}
	
	private static class DummyResource {
	}
}
