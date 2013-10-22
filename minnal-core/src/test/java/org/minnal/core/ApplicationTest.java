/**
 * 
 */
package org.minnal.core;

import static org.testng.Assert.assertNotNull;

import org.minnal.core.config.ApplicationConfiguration;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ApplicationTest {
	
	private DummyApplication application;
	
	@Test
	public void shouldGetResourceClass() {
		application = new DummyApplication() {
			@Override
			protected void defineResources() {
				addResource(DummyResource.class);
			}
		};
		application.init();
		assertNotNull(application.resource(DummyResource.class));
	}
	
	@Test
	public void shouldGetResourceSubClass() {
		application = new DummyApplication() {
			@Override
			protected void defineResources() {
				addResource(DummySubResource.class);
			}
		};
		application.init();
		assertNotNull(application.resource(DummyResource.class));
	}
	
	public static class DummyResource {
	}
	
	public static class DummySubResource extends DummyResource {
	}
	
	public static class DummyConfiguration extends ApplicationConfiguration {
	}
	
	public static class DummyApplication extends Application<DummyConfiguration> {
		@Override
		protected void registerPlugins() {
		}
		@Override
		protected void addFilters() {
		}
		@Override
		protected void defineRoutes() {
		}
		@Override
		protected void defineResources() {
		}
	}
}
