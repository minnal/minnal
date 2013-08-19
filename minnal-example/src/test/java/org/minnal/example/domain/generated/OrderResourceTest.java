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

	@Test
	public void readOrderTest() {
		org.minnal.example.domain.Order order = createDomain(org.minnal.example.domain.Order.class);
		order.persist();
		Response response = call(request("/orders/" + order.getId(),
				HttpMethod.GET,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(order)));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(serializer.deserialize(response.getContent(),
				org.minnal.example.domain.Order.class).getId(),
				order.getId());
	}

	@Test
	public void deleteOrderTest() {
		org.minnal.example.domain.Order order = createDomain(org.minnal.example.domain.Order.class);
		order.persist();
		Response response = call(request("/orders/" + order.getId(),
				HttpMethod.DELETE,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(order)));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		assertFalse(org.minnal.example.domain.Order.exists(order
				.getId()));
	}

	@Test
	public void createOrderOrderItemTest() {
		org.minnal.example.domain.Order order = createDomain(org.minnal.example.domain.Order.class);
		order.persist();
		org.minnal.example.domain.OrderItem orderItem = createDomain(org.minnal.example.domain.OrderItem.class);
		Response response = call(request("/orders/" + order.getId()
				+ "/order_items/", HttpMethod.POST,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(orderItem)));
		assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
	}

	@Test
	public void readOrderOrderItemTest() {
		org.minnal.example.domain.Order order = createDomain(org.minnal.example.domain.Order.class);
		order.persist();
		org.minnal.example.domain.OrderItem orderItem = createDomain(org.minnal.example.domain.OrderItem.class);
		order.collection("orderItems").add(orderItem);
		order.persist();
		Response response = call(request("/orders/" + order.getId()
				+ "/order_items/" + orderItem.getId(),
				HttpMethod.GET,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(orderItem)));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(serializer.deserialize(response.getContent(),
				org.minnal.example.domain.OrderItem.class)
				.getId(), orderItem.getId());
	}

	@Test
	public void deleteOrderOrderItemTest() {
		org.minnal.example.domain.Order order = createDomain(org.minnal.example.domain.Order.class);
		order.persist();
		org.minnal.example.domain.OrderItem orderItem = createDomain(org.minnal.example.domain.OrderItem.class);
		order.collection("orderItems").add(orderItem);
		order.persist();
		Response response = call(request("/orders/" + order.getId()
				+ "/order_items/" + orderItem.getId(),
				HttpMethod.DELETE,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(orderItem)));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		assertFalse(org.minnal.example.domain.OrderItem
				.exists(orderItem.getId()));
	}

	@Test
	public void createOrderPaymentTest() {
		org.minnal.example.domain.Order order = createDomain(org.minnal.example.domain.Order.class);
		order.persist();
		org.minnal.example.domain.Payment payment = createDomain(org.minnal.example.domain.Payment.class);
		Response response = call(request("/orders/" + order.getId()
				+ "/payments/", HttpMethod.POST,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(payment)));
		assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
	}

	@Test
	public void readOrderPaymentTest() {
		org.minnal.example.domain.Order order = createDomain(org.minnal.example.domain.Order.class);
		order.persist();
		org.minnal.example.domain.Payment payment = createDomain(org.minnal.example.domain.Payment.class);
		order.collection("payments").add(payment);
		order.persist();
		Response response = call(request("/orders/" + order.getId()
				+ "/payments/" + payment.getId(),
				HttpMethod.GET,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(payment)));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(serializer.deserialize(response.getContent(),
				org.minnal.example.domain.Payment.class)
				.getId(), payment.getId());
	}

	@Test
	public void deleteOrderPaymentTest() {
		org.minnal.example.domain.Order order = createDomain(org.minnal.example.domain.Order.class);
		order.persist();
		org.minnal.example.domain.Payment payment = createDomain(org.minnal.example.domain.Payment.class);
		order.collection("payments").add(payment);
		order.persist();
		Response response = call(request("/orders/" + order.getId()
				+ "/payments/" + payment.getId(),
				HttpMethod.DELETE,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(payment)));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		assertFalse(org.minnal.example.domain.Payment.exists(payment
				.getId()));
	}

}