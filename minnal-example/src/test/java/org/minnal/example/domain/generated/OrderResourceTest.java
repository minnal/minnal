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
public class OrderResourceTest extends BaseJPAResourceTest {
	@Test
	public void createOrderTest() {
		org.minnal.example.domain.Order order = createDomain(org.minnal.example.domain.Order.class);
		Response response = call(request("/orders/", HttpMethod.POST,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(order)));
		assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
	}
}