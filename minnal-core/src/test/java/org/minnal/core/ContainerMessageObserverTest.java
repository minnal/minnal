/**
 * 
 */
package org.minnal.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.ResourceConfiguration;
import org.minnal.core.config.RouteConfiguration;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.route.Route;
import org.minnal.core.server.MessageContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ContainerMessageObserverTest {

	private ContainerMessageObserver observer;
	
	private MessageListener listener;
	
	private MessageContext context;
	
	private Application<ApplicationConfiguration> application;
	
	private ResourceClass resourceClass;
	
	private Route route;
	
	private ApplicationConfiguration applicationConfiguration;
	
	private ResourceConfiguration resourceConfiguration;
	
	private RouteConfiguration routeConfiguration;
	
	@BeforeMethod
	public void setup() {
		observer = new ContainerMessageObserver();
		listener = mock(MessageListener.class);
		observer.registerListener(listener);
		application = mock(Application.class);
		applicationConfiguration = mock(ApplicationConfiguration.class);
		when(application.getConfiguration()).thenReturn(applicationConfiguration);
		resourceClass = mock(ResourceClass.class);
		resourceConfiguration = mock(ResourceConfiguration.class);
		when(resourceClass.getConfiguration()).thenReturn(resourceConfiguration);
		route = mock(Route.class);
		routeConfiguration = mock(RouteConfiguration.class);
		when(route.getConfiguration()).thenReturn(routeConfiguration);
		ApplicationContext.instance().clear();
		context = mock(MessageContext.class);
		when(context.getApplication()).thenReturn(application);
		when(context.getResourceClass()).thenReturn(resourceClass);
		when(context.getRoute()).thenReturn(route);
	}
	
	@Test
	public void shouldInvokeListenerOnReceived() {
		observer.onReceived(context);
		verify(listener).onReceived(context);
	}
	
	@Test
	public void shouldInvokeListenerOnComplete() {
		observer.onComplete(context);
		verify(listener).onComplete(context);
	}
	
	@Test
	public void shouldInvokeListenerOnError() {
		observer.onError(context);
		verify(listener).onError(context);
	}
	
	@Test
	public void shouldInvokeListenerOnSuccess() {
		observer.onSuccess(context);
		verify(listener).onSuccess(context);
	}
	
	@Test
	public void shouldInvokeListenerOnApplicationResolved() {
		observer.onApplicationResolved(context);
		verify(listener).onApplicationResolved(context);
	}
	
	@Test
	public void shouldSetApplicationConfigurationOnApplicationResolved() {
		observer.onApplicationResolved(context);
		assertEquals(ApplicationContext.instance().getApplicationConfiguration(), applicationConfiguration);
	}
	
	@Test
	public void shouldInvokeListenerOnRouteResolved() {
		observer.onRouteResolved(context);
		verify(listener).onRouteResolved(context);
	}
	
	@Test
	public void shouldSetRouteAndResourceConfigurationOnRouteResolved() {
		observer.onRouteResolved(context);
		assertEquals(ApplicationContext.instance().getRouteConfiguration(), routeConfiguration);
		assertEquals(ApplicationContext.instance().getResourceConfiguration(), resourceConfiguration);
	}
}
