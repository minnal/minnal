package org.minnal.example.domain.generated;

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
	public void createProductTest() {
		org.minnal.example.domain.Product product = createDomain(org.minnal.example.domain.Product.class);
		Response response = call(request("/products/", HttpMethod.POST,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(product)));
		assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
	}

	@Test
	public void readProductTest() {
		org.minnal.example.domain.Product product = createDomain(org.minnal.example.domain.Product.class);
		product.persist();
		Response response = call(request(
				"/products/" + product.getId(), HttpMethod.GET,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(product)));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(serializer.deserialize(response.getContent(),
				org.minnal.example.domain.Product.class)
				.getId(), product.getId());
	}

	@Test
	public void deleteProductTest() {
		org.minnal.example.domain.Product product = createDomain(org.minnal.example.domain.Product.class);
		product.persist();
		Response response = call(request(
				"/products/" + product.getId(),
				HttpMethod.DELETE,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(product)));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		assertFalse(org.minnal.example.domain.Product.exists(product
				.getId()));
	}

}