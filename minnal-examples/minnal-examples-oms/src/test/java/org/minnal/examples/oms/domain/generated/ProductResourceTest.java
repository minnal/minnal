package org.minnal.examples.oms.domain.generated;

import java.lang.reflect.Method;

import org.minnal.core.serializer.Serializer;
import org.minnal.core.resource.BaseJPAResourceTest;
import org.testng.annotations.Test;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.Response;
import static org.testng.Assert.*;

/**
 * This is an auto generated test class by minnal-generator
 */
public class ProductResourceTest extends BaseJPAResourceTest {
	@Test
	public void listProductTest() {
		for (Method method : org.minnal.examples.oms.domain.Product.class.getDeclaredMethods()) {
			System.out.println(method);
		}
		org.minnal.examples.oms.domain.Product product = createDomain(org.minnal.examples.oms.domain.Product.class);
		product.persist();
		product = org.minnal.examples.oms.domain.Product.findById(1L);
		Response response = call(request("/products/", HttpMethod.GET));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(
				serializer
						.deserializeCollection(
								response
										.getContent(),
								java.util.List.class,
								org.minnal.examples.oms.domain.Product.class)
						.size(),
				(int) org.minnal.examples.oms.domain.Product.count());
	}

	@Test
	public void createProductTest() {
		org.minnal.examples.oms.domain.Product product = createDomain(org.minnal.examples.oms.domain.Product.class);
		Response response = call(request("/products/", HttpMethod.POST,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(product)));
		assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
	}

	@Test
	public void updateProductTest() {
		org.minnal.examples.oms.domain.Product product = createDomain(org.minnal.examples.oms.domain.Product.class);
		product.persist();
		org.minnal.examples.oms.domain.Product modifiedproduct = createDomain(
				org.minnal.examples.oms.domain.Product.class, 1);
		Response response = call(request(
				"/products/" + product.getId(), HttpMethod.PUT,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(modifiedproduct)));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		product.merge();
		assertTrue(compare(modifiedproduct, product, 1));
	}

	@Test
	public void readProductTest() {
		org.minnal.examples.oms.domain.Product product = createDomain(org.minnal.examples.oms.domain.Product.class);
		product.persist();
		Response response = call(request(
				"/products/" + product.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(serializer.deserialize(response.getContent(),
				org.minnal.examples.oms.domain.Product.class)
				.getId(), product.getId());
	}

	@Test
	public void deleteProductTest() {
		org.minnal.examples.oms.domain.Product product = createDomain(org.minnal.examples.oms.domain.Product.class);
		product.persist();
		Response response = call(request(
				"/products/" + product.getId(),
				HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		response = call(request("/products/" + product.getId(),
				HttpMethod.GET,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(product)));
		assertEquals(response.getStatus(), HttpResponseStatus.NOT_FOUND);
	}

	public static void main(String[] args) {
		for (Method method : org.minnal.examples.oms.domain.Product.class.getDeclaredMethods()) {
			System.out.println(method);
		}
	}
}