/**
 * 
 */
package org.minnal.core.resource;

import static org.testng.Assert.assertEquals;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.config.ResourceConfiguration;
import org.minnal.core.route.Route;
import org.minnal.core.route.RouteBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ResourceClassTest {
	
	private ResourceConfiguration config;
	
	@BeforeMethod
	public void setup() {
		config = new ResourceConfiguration("test");
	}

	@Test
	public void shouldCreateResourceClass() {
		ResourceClass resourceClass = new ResourceClass(DummyResource.class, config);
		assertEquals(resourceClass.getConfiguration(), config);
	}
	
	@Test
	public void shouldCreateRouteBuilder() {
		ResourceClass resourceClass = new ResourceClass(DummyResource.class, config);
		RouteBuilder builder = resourceClass.builder("/orders");
		builder.action(HttpMethod.GET, "methodWithValidParameters");
		Route route = builder.build().get(0);
		assertEquals(route.getConfiguration().getParent(), config);
	}
}
