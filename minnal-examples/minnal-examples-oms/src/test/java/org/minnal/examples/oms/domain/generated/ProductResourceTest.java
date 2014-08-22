package org.minnal.examples.oms.domain.generated;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.jaxrs.test.BaseJPAResourceTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This is an auto generated test class by minnal-generator
 */
public class ProductResourceTest extends BaseJPAResourceTest {
	@Test
	public void listProductTest() {
		org.minnal.examples.oms.domain.Product product = createDomain(org.minnal.examples.oms.domain.Product.class);
		product.persist();
		FullHttpResponse response = call(request("/products/",
				HttpMethod.GET));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(deserializeCollection(response.content(),
				java.util.List.class,
				org.minnal.examples.oms.domain.Product.class)
				.size(),
				(int) org.minnal.examples.oms.domain.Product
						.count());
	}

	@Test
	public void readProductTest() {
		org.minnal.examples.oms.domain.Product product = createDomain(org.minnal.examples.oms.domain.Product.class);
		product.persist();
		FullHttpResponse response = call(request(
				"/products/" + product.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(deserialize(response.content(),
				org.minnal.examples.oms.domain.Product.class)
				.getId(), product.getId());
	}

	@Test
	public void createProductTest() {
		org.minnal.examples.oms.domain.Product product = createDomain(org.minnal.examples.oms.domain.Product.class);
		FullHttpResponse response = call(request("/products/",
				HttpMethod.POST, serialize(product)));
		assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
	}

	@Test
	public void updateProductTest() {
		org.minnal.examples.oms.domain.Product product = createDomain(org.minnal.examples.oms.domain.Product.class);
		product.persist();
		org.minnal.examples.oms.domain.Product modifiedproduct = createDomain(
				org.minnal.examples.oms.domain.Product.class, 1);
		FullHttpResponse response = call(request(
				"/products/" + product.getId(), HttpMethod.PUT,
				serialize(modifiedproduct)));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		product.merge();
		assertTrue(compare(modifiedproduct, product, 1));
	}

	@Test
	public void deleteProductTest() {
		org.minnal.examples.oms.domain.Product product = createDomain(org.minnal.examples.oms.domain.Product.class);
		product.persist();
		FullHttpResponse response = call(request(
				"/products/" + product.getId(),
				HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		response = call(request("/products/" + product.getId(),
				HttpMethod.GET, serialize(product)));
		assertEquals(response.getStatus(), HttpResponseStatus.NOT_FOUND);
	}

}