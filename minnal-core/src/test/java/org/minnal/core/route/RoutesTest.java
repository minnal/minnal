/**
 * 
 */
package org.minnal.core.route;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.config.RouteConfiguration;
import org.minnal.core.server.ServerRequest;
import org.minnal.core.server.exception.MethodNotAllowedException;
import org.minnal.core.server.exception.NotAcceptableException;
import org.minnal.core.server.exception.NotFoundException;
import org.minnal.core.server.exception.UnsupportedMediaTypeException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;
import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
public class RoutesTest {
	
	private RouteBuilder builder;
	
	private ServerRequest request;
	
	private Routes routes;
	
	private RouteConfiguration configuration;
	
	private Route route1;
	
	private Route route2;
	
	@BeforeMethod
	public void setup() {
		builder = mock(RouteBuilder.class);
		request = mock(ServerRequest.class);
		when(request.getContentType()).thenReturn(MediaType.JSON_UTF_8);
		routes = new Routes();
		configuration = new RouteConfiguration("test");
		configuration.setDefaultMediaType(MediaType.JSON_UTF_8);
		route1 = new Route(new RoutePattern("/orders/{order_id}/order_items/{id}"), HttpMethod.GET, null, configuration, null);
		route2 = new Route(new RoutePattern("/orders/{order_id}/order_items/{id}"), HttpMethod.PUT, null, configuration, null);
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
	public void shouldResolveRequestAndSetSupportedAccepts() {
		when(request.getRelativePath()).thenReturn("/orders/1/order_items/1");
		when(request.getHttpMethod()).thenReturn(HttpMethod.PUT);
		routes.addRoute(builder);
		routes.resolve(request);
		verify(request).setSupportedAccepts(Sets.newHashSet(MediaType.JSON_UTF_8, MediaType.XML_UTF_8));
	}
	
	@Test
	public void shouldResolveRequestWithValidAccepts() {
		when(request.getRelativePath()).thenReturn("/orders/1/order_items/1");
		when(request.getHttpMethod()).thenReturn(HttpMethod.PUT);
		when(request.getAccepts()).thenReturn(Sets.newHashSet(MediaType.JSON_UTF_8, MediaType.XML_UTF_8, MediaType.ANY_AUDIO_TYPE));
		routes.addRoute(builder);
		assertEquals(routes.resolve(request), route2);
	}
	
	@Test(expectedExceptions=NotFoundException.class)
	public void shouldReturnNotFoundForNonExistingPath() {
		when(request.getRelativePath()).thenReturn("/orders/1");
		when(request.getHttpMethod()).thenReturn(HttpMethod.GET);
		routes.addRoute(builder);
		routes.resolve(request);
	}
	
	@Test
	public void shouldReturnMethodNotAllowedForInvalidMethod() {
		when(request.getRelativePath()).thenReturn("/orders/1/order_items/1");
		when(request.getHttpMethod()).thenReturn(HttpMethod.POST);
		routes.addRoute(builder);
		try {
			routes.resolve(request);
			fail("Expected MethodNotAllowedException but didn't catch one");
		} catch (MethodNotAllowedException e) {
			assertEquals(e.getAllowedMethods(), Sets.newHashSet(HttpMethod.GET, HttpMethod.PUT));
		}
	}
	
	@Test
	public void shouldReturnUnsupportedMediaTypeForInvalidContentType() {
		when(request.getRelativePath()).thenReturn("/orders/1/order_items/1");
		when(request.getHttpMethod()).thenReturn(HttpMethod.PUT);
		when(request.getContentType()).thenReturn(MediaType.ANY_APPLICATION_TYPE);
		routes.addRoute(builder);
		try {
			routes.resolve(request);
			fail("Expected UnsupportedMediaTypeException but didn't catch one");
		} catch (UnsupportedMediaTypeException e) {
			assertEquals(e.getExpectedTypes(), Sets.newHashSet(MediaType.JSON_UTF_8, MediaType.XML_UTF_8));
		}
	}
	
	@Test
	public void shouldReturnNotAcceptableForInvalidAccepts() {
		when(request.getRelativePath()).thenReturn("/orders/1/order_items/1");
		when(request.getHttpMethod()).thenReturn(HttpMethod.PUT);
		when(request.getAccepts()).thenReturn(Sets.newHashSet(MediaType.ANY_AUDIO_TYPE));
		routes.addRoute(builder);
		try {
			routes.resolve(request);
			fail("Expected NotAcceptableException but didn't catch one");
		} catch (NotAcceptableException e) {
			assertEquals(e.getExpectedTypes(), Sets.newHashSet(MediaType.JSON_UTF_8, MediaType.XML_UTF_8));
		}
	}
	
	@Test
	public void shouldResolveRouteWithWildCardAccepts() {
		when(request.getRelativePath()).thenReturn("/orders/1/order_items/1");
		when(request.getHttpMethod()).thenReturn(HttpMethod.PUT);
		when(request.getAccepts()).thenReturn(Sets.newHashSet(MediaType.ANY_APPLICATION_TYPE, MediaType.ANY_IMAGE_TYPE));
		routes.addRoute(builder);
		assertEquals(routes.resolve(request), route2);
	}
	
	@Test
	public void shouldResolveRouteWithoutContentTypeForGetHttpMethod() {
		when(request.getRelativePath()).thenReturn("/orders/1/order_items/1");
		when(request.getHttpMethod()).thenReturn(HttpMethod.GET);
		when(request.getContentType()).thenReturn(null);
		routes.addRoute(builder);
		assertEquals(routes.resolve(request), route1);
	}
	
	@Test
	public void shouldResolveRouteWithoutContentTypeForDeleteHttpMethod() {
		Route route = new Route(new RoutePattern("/orders/{order_id}/order_items/{id}"), HttpMethod.DELETE, null, configuration, null);
		when(builder.build()).thenReturn(Arrays.asList(route));
		when(request.getRelativePath()).thenReturn("/orders/1/order_items/1");
		when(request.getHttpMethod()).thenReturn(HttpMethod.DELETE);
		when(request.getContentType()).thenReturn(null);
		routes.addRoute(builder);
		assertEquals(routes.resolve(request), route);
	}
}
