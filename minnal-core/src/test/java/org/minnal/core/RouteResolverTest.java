/**
 * 
 */
package org.minnal.core;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.testng.Assert.*;

import java.util.Arrays;

import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.route.Route;
import org.minnal.core.route.RoutePattern;
import org.minnal.core.route.Routes;
import org.minnal.core.server.MessageContext;
import org.minnal.core.server.ServerRequest;
import org.minnal.core.server.ServerResponse;
import org.minnal.core.server.exception.NotFoundException;
import org.minnal.core.util.HttpUtil;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.beust.jcommander.internal.Maps;

/**
 * @author ganeshs
 *
 */
public class RouteResolverTest {
	
	private MessageContext context;
	
	private ResourceClass resourceClass; 
	
	private RouteResolver resolver;
	
	private Route route;
	
	private Application<ApplicationConfiguration> application;
	
	@BeforeMethod
	public void setup() {
		ServerRequest request = mock(ServerRequest.class);
		ServerResponse response = mock(ServerResponse.class);
		application = mock(Application.class);
		context = mock(MessageContext.class);
		when(context.getRequest()).thenReturn(request);
		when(context.getResponse()).thenReturn(response);
		when(context.getApplication()).thenReturn(application);
		
		Routes routes = mock(Routes.class);
		route = mock(Route.class);
		RoutePattern pattern = mock(RoutePattern.class);
		when(route.getRoutePattern()).thenReturn(pattern);
		when(pattern.match(anyString())).thenReturn(Maps.newHashMap("key", "value"));
		when(routes.resolve(context.getRequest())).thenReturn(route);
		resolver = spy(new RouteResolver());
		resourceClass = mock(ResourceClass.class);
		doReturn(resourceClass).when(resolver).resolveResource(application, request);
		when(application.getRoutes(resourceClass)).thenReturn(routes);
	}

	@Test
	public void shouldPopulateContextWithResourceclass() {
		resolver.resolve(context);
		verify(context).setResourceClass(resourceClass);
	}
	
	@Test
	public void shouldPopulateContextWithRoute() {
		resolver.resolve(context);
		verify(context).setRoute(route);
	}
	
	@Test
	public void shouldPopulateRequestWithRoute() {
		resolver.resolve(context);
		verify(context.getRequest()).setResolvedRoute(route);
	}
	
	@Test
	public void shouldPopulateResponseWithRoute() {
		resolver.resolve(context);
		verify(context.getResponse()).setResolvedRoute(route);
	}
	
	@Test
	public void shouldPopulateRequestWithPathParameters() {
		resolver.resolve(context);
		verify(context.getRequest()).addHeaders(Maps.newHashMap("key", "value"));
	}
	
	@Test
	public void shouldResolveResource() {
		Request request = mock(Request.class);
		when(request.getUri()).thenReturn(HttpUtil.createURI("/app1/resource1/path1"));
		ResourceClass clazz1 = mock(ResourceClass.class);
		ResourceClass clazz2 = mock(ResourceClass.class);
		when(clazz1.getBasePath()).thenReturn("/resource1");
		when(clazz2.getBasePath()).thenReturn("/resource2");
		ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
		when(application.getConfiguration()).thenReturn(configuration);
		when(configuration.getBasePath()).thenReturn("/app1");
		when(application.getResources()).thenReturn(Arrays.asList(clazz1, clazz2));
		assertEquals(resolver.resolveResource(application, request), clazz1);
	}
}
