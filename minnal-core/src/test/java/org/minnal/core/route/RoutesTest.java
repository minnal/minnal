/**
 * 
 */
package org.minnal.core.route;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.server.ServerRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class RoutesTest {
	
	private RouteBuilder builder;
	
	private ServerRequest request;
	
	private Routes routes;
	
	private Route route1;
	
	private Route route2;
	
	@BeforeMethod
	public void setup() {
		builder = mock(RouteBuilder.class);
		request = mock(ServerRequest.class);
		routes = new Routes();
		route1 = new Route(new RoutePattern("/orders/{order_id}/order_items/{id}"), HttpMethod.GET, null, null, null);
		route2 = new Route(new RoutePattern("/orders/{order_id}/order_items/{id}"), HttpMethod.PUT, null, null, null);
		when(builder.build()).thenReturn(Arrays.asList(route1, route2));
	}

	@Test
	public void shouldAddRoute() {
		routes.addRoute(builder);
		assertEquals(routes.getRoutes().size(), 2);
	}
	
	@Test
	public void shouldGetAllowedMethods() {
		when(request.getRelativePath()).thenReturn("/orders/1/order_items/1");
		routes.addRoute(builder);
		assertEquals(routes.getAllowedMethods(request), new HashSet<HttpMethod>(Arrays.asList(HttpMethod.GET, HttpMethod.PUT)));
	}
	
	@Test
	public void shouldReturnEmptySetOfAllowedMethodsForNonExistingPath() {
		when(request.getRelativePath()).thenReturn("/orders/1");
		routes.addRoute(builder);
		assertTrue(routes.getAllowedMethods(request).isEmpty());
	}
	
	@Test
	public void shouldResolveRequest() {
		when(request.getRelativePath()).thenReturn("/orders/1/order_items/1");
		when(request.getHttpMethod()).thenReturn(HttpMethod.PUT);
		routes.addRoute(builder);
		assertEquals(routes.resolve(request), route2);
	}
	
	@Test
	public void shouldNotResolveRequestForNonExistingPath() {
		when(request.getRelativePath()).thenReturn("/orders/1");
		when(request.getHttpMethod()).thenReturn(HttpMethod.PUT);
		routes.addRoute(builder);
		assertNull(routes.resolve(request));
	}
}
