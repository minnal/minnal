/**
 * 
 */
package org.minnal.core.route;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.MinnalException;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.config.ResourceConfiguration;
import org.minnal.core.config.RouteConfiguration;
import org.minnal.core.resource.ResourceClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

/**
 * @author ganeshs
 *
 */
public class RouteBuilderTest {
	
	private Object resource;
	
	private ResourceClass resourceClass;
	
	private RouteBuilder builder;
	
	@BeforeMethod
	public void setup() {
		resource = mock(DummyResource.class);
		resourceClass = new ResourceClass(new ResourceConfiguration("dummy"), resource.getClass());
		builder = new RouteBuilder(resourceClass, "/orders/{order_id}/order_items/{id}");
	}
	
	@Test(expectedExceptions=MinnalException.class)
	public void shouldThrowExceptionWhenActionCalledForMethodWithoutParams() {
		builder.action(HttpMethod.GET, "methodWithoutParameters");
	}
	
	@Test(expectedExceptions=MinnalException.class)
	public void shouldThrowExceptionWhenActionCalledForMethodWithAdditionalParams() {
		builder.action(HttpMethod.GET, "methodWithAdditionalParameters");
	}
	
	@Test(expectedExceptions=IllegalStateException.class)
	public void shouldThrowExceptionWhenBuiltWithoutCallingAction() {
		builder.build();
	}
	
	@Test
	public void shouldBuildRouteWithDefaultRequestAndResponseType() {
		builder.action(HttpMethod.GET, "methodWithValidParameters");
		List<Route> routes = builder.build();
		assertEquals(routes.get(1).getRequestType(), Object.class);
		assertEquals(routes.get(1).getResponseType(), Object.class);
	}
	
	@Test
	public void shouldBuildRouteWithDefinedRequestAndResponseType() {
		builder.action(HttpMethod.GET, "methodWithValidParameters", new TypeToken<List<String>>(){}.getType(), Void.class);
		List<Route> routes = builder.build();
		assertEquals(routes.get(0).getRequestType(), new TypeToken<List<String>>(){}.getType());
		assertEquals(routes.get(0).getResponseType(), Void.class);
	}
	
	@Test
	public void shouldAddOptionsActionOnBuild() {
		builder.action(HttpMethod.GET, "methodWithValidParameters");
		List<Route> routes = builder.build();
		assertEquals(routes.size(), 2);
		assertEquals(routes.get(1).getMethod(), HttpMethod.OPTIONS);
		assertEquals(routes.get(1).getAction(), null);
	}
	
	@Test
	public void shouldBuildRouteWhenActionCalledWithValidParams() throws Exception {
		builder.action(HttpMethod.GET, "methodWithValidParameters");
		List<Route> routes = builder.build();
		assertEquals(routes.size(), 2);
		assertEquals(routes.get(0).getAction(), new Action(resource, resource.getClass().getDeclaredMethod("methodWithValidParameters", Request.class, Response.class)));
	}
	
	@Test
	public void shouldBuildRouteWithMultipleActions() throws Exception {
		builder.action(HttpMethod.GET, "methodWithValidParameters");
		builder.action(HttpMethod.PUT, "methodWithValidParameters");
		List<Route> routes = builder.build();
		assertEquals(routes.size(), 3);
		assertEquals(routes.get(0).getAction(), new Action(resource, resource.getClass().getDeclaredMethod("methodWithValidParameters", Request.class, Response.class)));
		assertEquals(routes.get(0).getMethod(), HttpMethod.GET);
		assertEquals(routes.get(1).getAction(), new Action(resource, resource.getClass().getDeclaredMethod("methodWithValidParameters", Request.class, Response.class)));
		assertEquals(routes.get(1).getMethod(), HttpMethod.PUT);
	}
	
	@Test
	public void shouldBuildRouteWithCustomConfiguration() {
		builder.action(HttpMethod.GET, "methodWithValidParameters");
		builder.using(new RouteConfiguration("test"));
		List<Route> routes = builder.build();
		assertEquals(routes.size(), 2);
		assertEquals(routes.get(0).getConfiguration().getName(), "test");
	}
	
	@Test
	public void shouldBuildRouteWithMultipleAttributes() {
		builder.action(HttpMethod.GET, "methodWithValidParameters");
		builder.attribute("testKey1", "testValue1");
		builder.attribute("testKey2", "testValue2");
		List<Route> routes = builder.build();
		assertEquals(routes.size(), 2);
		assertEquals(routes.get(0).getAttributes().size(), 2);
		assertEquals(routes.get(0).getAttributes().get("testKey1"), "testValue1");
		assertEquals(routes.get(0).getAttributes().get("testKey2"), "testValue2");
	}
	
	@Test
	public void shouldBuildRouteWithAttributeMap() {
		builder.action(HttpMethod.GET, "methodWithValidParameters");
		Map<String, String> attributes = new HashMap<String, String>();
		attributes.put("testKey1", "testValue1");
		attributes.put("testKey2", "testValue2");
		builder.attributes(attributes);
		builder.attribute("testKey3", "testValue3");
		List<Route> routes = builder.build();
		assertEquals(routes.size(), 2);
		assertEquals(routes.get(0).getAttributes().size(), 3);
		assertEquals(routes.get(0).getAttributes().get("testKey1"), "testValue1");
		assertEquals(routes.get(0).getAttributes().get("testKey2"), "testValue2");
		assertEquals(routes.get(0).getAttributes().get("testKey3"), "testValue3");
	}
	
	private interface DummyResource {
		void methodWithoutParameters();
		void methodWithValidParameters(Request request, Response response);
		void methodWithAdditionalParameters(Request request, Response response, Object additionalParam);
	}

}
