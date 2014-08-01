package org.minnal.examples.oms.domain.generated;

import static org.testng.Assert.assertEquals;
import io.netty.handler.codec.http.HttpResponseStatus;

import org.minnal.core.Response;
import org.minnal.core.resource.BaseJPAResourceTest;
import org.minnal.core.serializer.Serializer;
import org.testng.annotations.Test;

/**
 * This is an auto generated test class by minnal-generator
 */
public class OrderResourceTest extends BaseJPAResourceTest {
	@Test
	public void createOrderTest() {
		org.minnal.examples.oms.domain.Order order = createDomain(org.minnal.examples.oms.domain.Order.class);
		Response response = call(request("/orders/", HttpMethod.POST,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(order)));
		assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
	}
}