/**
 * 
 */
package org.minnal.core.resource;

import static org.testng.Assert.*;

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
	public void shouldCreateResourceClassForAResourceWithDefaultBasePath() {
		ResourceClass resourceClass = new ResourceClass(config, DummyResource.class);
		assertEquals(resourceClass.getBasePath(), "");
	}
	
	@Test
	public void shouldCreateResourceClassForAResourceWithSuppliedBasePath() {
		ResourceClass resourceClass = new ResourceClass(config, DummyResource.class, "/test");
		assertEquals(resourceClass.getBasePath(), "/test");
	}
	
	@Test
	public void shouldCreateResourceClassForAResourceWithEmptyBasePath() {
		ResourceClass resourceClass = new ResourceClass(config, DummyResource.class, "/");
		assertEquals(resourceClass.getBasePath(), "");
	}

	@Test
	public void shouldCreateResourceClassForAResource() {
		ResourceClass resourceClass = new ResourceClass(config, DummyResource.class);
		assertEquals(resourceClass.getConfiguration(), config);
		assertEquals(resourceClass.getResourceClass(), DummyResource.class);
		assertNull(resourceClass.getEntityClass());
	}
	
	@Test
	public void shouldCreateResourceClassForAnEntityWithDefaultBasePath() {
		ResourceClass resourceClass = new ResourceClass(DummyModel.class, config);
		assertEquals(resourceClass.getBasePath(), "/dummy_models");
	}
	
	@Test
	public void shouldCreateResourceClassForAnEntityWithSuppliedBasePath() {
		ResourceClass resourceClass = new ResourceClass(DummyModel.class, config, "/test");
		assertEquals(resourceClass.getBasePath(), "/test");
	}
	
	@Test
	public void shouldCreateResourceClassForAnEntityWithEmptyBasePath() {
		ResourceClass resourceClass = new ResourceClass(DummyModel.class, config, "/");
		assertEquals(resourceClass.getBasePath(), "");
	}
	
	@Test
	public void shouldCreateResourceClassForAnEntity() {
		ResourceClass resourceClass = new ResourceClass(DummyModel.class, config);
		assertEquals(resourceClass.getConfiguration(), config);
		assertEquals(resourceClass.getEntityClass(), DummyModel.class);
		assertNull(resourceClass.getResourceClass());
	}
	
	@Test
	public void shouldCreateRouteBuilder() {
		ResourceClass resourceClass = new ResourceClass(config, DummyResource.class);
		RouteBuilder builder = resourceClass.builder("/orders");
		builder.action(HttpMethod.GET, "methodWithValidParameters");
		Route route = builder.build().get(0);
		assertEquals(route.getConfiguration().getParent(), config);
	}
	
	public static class DummyModel {
		
	}
}
