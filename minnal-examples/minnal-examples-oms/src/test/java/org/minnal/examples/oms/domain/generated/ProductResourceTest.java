package org.minnal.examples.oms.domain.generated;

import org.glassfish.jersey.server.ContainerResponse;
import org.minnal.core.resource.BaseMinnalResourceTest;
import org.testng.annotations.Test;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;
import static org.testng.Assert.*;

/**
 * This is an auto generated test class by minnal-generator
 */
public class ProductResourceTest extends BaseMinnalResourceTest {
	@Test
	public void listProductTest() {
		org.minnal.examples.oms.domain.Product product = createDomain(org.minnal.examples.oms.domain.Product.class);
		product.persist();
		ContainerResponse response = call(request("/products/",
				HttpMethod.GET));
		assertEquals(response.getStatus(), Response.Status.OK
				.getStatusCode());
		assertEquals(deserializeCollection(getContent(response),
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
		ContainerResponse response = call(request("/products/"
				+ product.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(), Response.Status.OK
				.getStatusCode());
		assertEquals(deserialize(getContent(response),
				org.minnal.examples.oms.domain.Product.class)
				.getId(), product.getId());
	}

	@Test
	public void createProductTest() {
		org.minnal.examples.oms.domain.Product product = createDomain(org.minnal.examples.oms.domain.Product.class);
		ContainerResponse response = call(request("/products/",
				HttpMethod.POST, serialize(product)));
		assertEquals(response.getStatus(), Response.Status.CREATED
				.getStatusCode());
	}

	@Test
	public void updateProductTest() {
		org.minnal.examples.oms.domain.Product product = createDomain(org.minnal.examples.oms.domain.Product.class);
		product.persist();
		org.minnal.examples.oms.domain.Product modifiedproduct = createDomain(
				org.minnal.examples.oms.domain.Product.class, 1);
		ContainerResponse response = call(request("/products/"
				+ product.getId(), HttpMethod.PUT,
				serialize(modifiedproduct)));
		assertEquals(response.getStatus(), Response.Status.NO_CONTENT
				.getStatusCode());
		product.merge();
		assertTrue(compare(modifiedproduct, product, 1));
	}

	@Test
	public void deleteProductTest() {
		org.minnal.examples.oms.domain.Product product = createDomain(org.minnal.examples.oms.domain.Product.class);
		product.persist();
		ContainerResponse response = call(request("/products/"
				+ product.getId(), HttpMethod.DELETE));
		assertEquals(response.getStatus(), Response.Status.NO_CONTENT
				.getStatusCode());
		response = call(request("/products/" + product.getId(),
				HttpMethod.GET, serialize(product)));
		assertEquals(response.getStatus(), Response.Status.NOT_FOUND
				.getStatusCode());
	}

}