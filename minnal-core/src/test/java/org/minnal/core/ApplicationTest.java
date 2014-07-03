/**
 * 
 */
package org.minnal.core;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;

import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.server.exception.ApplicationException;
import org.minnal.core.server.exception.ExceptionHandler;
import org.minnal.core.server.exception.InternalServerErrorException;
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
	
	@Test
	public void shouldMapExceptions() {
		application = new DummyApplication() {
			@Override
			protected void mapExceptions() {
				super.mapExceptions();
				addExceptionMapping(NullPointerException.class, InternalServerErrorException.class);
			}
		};
		application.init();
		assertTrue(application.getExceptionResolver().getMappedException(new NullPointerException()) instanceof InternalServerErrorException);
	}
	
	@Test
	public void shouldAddExceptionHandler() {
		application = new DummyApplication();
		final AtomicBoolean bool = new AtomicBoolean(false);
		application.addExceptionHandler(NullPointerException.class, new ExceptionHandler() {
			@Override
			public void handle(Request request, Response response, Throwable exception) {
				bool.set(true);
			}
		});
		application.init();
		application.getExceptionResolver().resolve(mock(Request.class), mock(Response.class), new NullPointerException());
		assertTrue(bool.get());
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
