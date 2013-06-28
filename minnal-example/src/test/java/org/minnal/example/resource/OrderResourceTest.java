/**
 * 
 */
package org.minnal.example.resource;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.resource.BaseResourceTest;
import org.minnal.core.server.MessageContext;
import org.minnal.core.server.ServerRequest;
import org.minnal.core.server.ServerResponse;
import org.minnal.example.domain.Order;
import org.testng.annotations.Test;


/**
 * @author ganeshs
 *
 */
public class OrderResourceTest extends BaseResourceTest {

	private ServerRequest request;
	
	private ServerResponse response;
	
	@Test
	public void shouldCreateOrder() {
		request = request("/orders", HttpMethod.POST, "{\"customer_email\":\"ganeshs@flipkart.com\"}");
		response = response(request);
		MessageContext context = new MessageContext(request, response);
		route(context);
		assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
		Order order = serializer.deserialize(response.getContent(), Order.class);
		assertEquals(order.getCustomerEmail(), "ganeshs@flipkart.com");
		assertNull(order.getOrderItems());
	}

	public void updateOrders(org.minnal.core.Request request,
			org.minnal.core.Response response) {
		org.minnal.example.domain.Order orders = org.minnal.example.domain.Order
				.first(new Object[] { "id", request.getHeader("id") });
		if (orders == null) {
			throw new org.minnal.core.server.exception.NotFoundException(
					"orders with id " + request.getHeader("id") + " not found");
		}
		org.minnal.instrument.util.DynaBean dynaBean = request
				.getContentAs(org.minnal.instrument.util.DynaBean.class);
		orders.updateAttributes(dynaBean.getAttributes());
	}
	
	public Object createOrders(org.minnal.core.Request request,
			org.minnal.core.Response response) {
		org.minnal.example.domain.Order orders = request
				.getContentAs(org.minnal.example.domain.Order.class);
		orders.persist();
		return orders;
	}
}
