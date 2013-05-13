/**
 * 
 */
package org.minnal.core;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.route.Route;
import org.minnal.core.route.RoutePattern;
import org.minnal.core.route.Routes;
import org.minnal.core.server.MessageContext;
import org.minnal.core.server.ServerRequest;
import org.minnal.core.server.ServerResponse;
import org.minnal.core.server.exception.NotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.beust.jcommander.internal.Maps;

/**
 * @author ganeshs
 *
 */
public class RouteResolverTest {
	
	private ApplicationMapping applicationMapping;
	
	private MessageContext context;
	
	private ServerRequest request;
	
	private ServerResponse response;
	
	private RouteResolver resolver;
	
	private Route route;
	
	private Application<ApplicationConfiguration> application;
	
	@BeforeMethod
	public void setup() {
		request = mock(ServerRequest.class);
		response = mock(ServerResponse.class);
		context = mock(MessageContext.class);
		when(context.getRequest()).thenReturn(request);
		when(context.getResponse()).thenReturn(response);
		applicationMapping = mock(ApplicationMapping.class);
		application = mock(Application.class);
		when(applicationMapping.resolve(context.getRequest())).thenReturn(application);
		Routes routes = mock(Routes.class);
		route = mock(Route.class);
		RoutePattern pattern = mock(RoutePattern.class);
		when(route.getRoutePattern()).thenReturn(pattern);
		when(pattern.match(anyString())).thenReturn(Maps.newHashMap("key", "value"));
		when(application.getRoutes()).thenReturn(routes);
		when(routes.resolve(request)).thenReturn(route);
		resolver = new RouteResolver(applicationMapping);
	}

	@Test(expectedExceptions=NotFoundException.class)
	public void shouldReturnNotFoundIfApplicationDoesntMatch() {
		when(applicationMapping.resolve(context.getRequest())).thenReturn(null);
		resolver.resolve(context);
	}
	
	@Test
	public void shouldPopulateContextWithApplication() {
		resolver.resolve(context);
		verify(context).setApplication(application);
	}
	
	@Test
	public void shouldPopulateContextWithRoute() {
		resolver.resolve(context);
		verify(context).setRoute(route);
	}
	
	@Test
	public void shouldPopulateRequestWithRoute() {
		resolver.resolve(context);
		verify(request).setResolvedRoute(route);
	}
	
	@Test
	public void shouldPopulateResponseWithRoute() {
		resolver.resolve(context);
		verify(response).setResolvedRoute(route);
	}
	
	@Test
	public void shouldPopulateRequestWithPathParameters() {
		resolver.resolve(context);
		verify(request).addHeaders(Maps.newHashMap("key", "value"));
	}
}
