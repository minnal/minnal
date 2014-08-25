package org.minnal.examples.oms.domain.generated;

import org.glassfish.jersey.server.ContainerResponse;
import org.minnal.core.resource.BaseMinnalResourceTest;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

import static org.testng.Assert.*;

/**
 * This is an auto generated test class by minnal-generator
 */
public class OrderResourceTest extends BaseMinnalResourceTest {
	@Test
	public void listOrderTest() {
		org.minnal.examples.oms.domain.Order order = createDomain(org.minnal.examples.oms.domain.Order.class);
		order.persist();
		ContainerResponse response = call(request("/orders/",
				HttpMethod.GET));
		assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
		assertEquals(deserializeCollection(
				getByteBufferFromContainerResp(response),
				java.util.List.class,
				org.minnal.examples.oms.domain.Order.class)
				.size(),
				(int) org.minnal.examples.oms.domain.Order
						.count());
	}

	@Test
	public void readOrderTest() {
		org.minnal.examples.oms.domain.Order order = createDomain(org.minnal.examples.oms.domain.Order.class);
		order.persist();
		ContainerResponse response = call(request("/orders/"
				+ order.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
		assertEquals(deserialize(
				getByteBufferFromContainerResp(response),
				org.minnal.examples.oms.domain.Order.class)
				.getId(), order.getId());
	}

	@Test
	public void createOrderTest() {
		org.minnal.examples.oms.domain.Order order = createDomain(org.minnal.examples.oms.domain.Order.class);
		ContainerResponse response = call(request("/orders/",
				HttpMethod.POST, serialize(order)));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_CREATED);
	}

	@Test
	public void updateOrderTest() {
		org.minnal.examples.oms.domain.Order order = createDomain(org.minnal.examples.oms.domain.Order.class);
		order.persist();
		org.minnal.examples.oms.domain.Order modifiedorder = createDomain(
				org.minnal.examples.oms.domain.Order.class, 1);
		ContainerResponse response = call(request("/orders/"
				+ order.getId(), HttpMethod.PUT,
				serialize(modifiedorder)));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_NO_CONTENT);
		order.merge();
		assertTrue(compare(modifiedorder, order, 1));
	}

	@Test
	public void deleteOrderTest() {
		org.minnal.examples.oms.domain.Order order = createDomain(org.minnal.examples.oms.domain.Order.class);
		order.persist();
		ContainerResponse response = call(request("/orders/"
				+ order.getId(), HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_NO_CONTENT);
		response = call(request("/orders/" + order.getId(),
				HttpMethod.GET, serialize(order)));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void listOrderPaymentTest() {
		org.minnal.examples.oms.domain.Order order = createDomain(org.minnal.examples.oms.domain.Order.class);
		order.persist();

		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		order.collection("payments").add(payment);
		order.persist();

		ContainerResponse response = call(request("/orders/"
				+ order.getId() + "/payments/", HttpMethod.GET));
		assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
		assertEquals(deserializeCollection(
				getByteBufferFromContainerResp(response),
				java.util.List.class,
				org.minnal.examples.oms.domain.Payment.class)
				.size(), order.getPayments().size());
	}

	@Test
	public void readOrderPaymentTest() {
		org.minnal.examples.oms.domain.Order order = createDomain(org.minnal.examples.oms.domain.Order.class);
		order.persist();
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		order.collection("payments").add(payment);
		order.persist();
		ContainerResponse response = call(request("/orders/"
				+ order.getId() + "/payments/"
				+ payment.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
		assertEquals(deserialize(
				getByteBufferFromContainerResp(response),
				org.minnal.examples.oms.domain.Payment.class)
				.getId(), payment.getId());
	}

	@Test
	public void createOrderPaymentTest() {
		org.minnal.examples.oms.domain.Order order = createDomain(org.minnal.examples.oms.domain.Order.class);
		order.persist();
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		ContainerResponse response = call(request("/orders/"
				+ order.getId() + "/payments/",
				HttpMethod.POST, serialize(payment)));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_CREATED);
	}

	@Test
	public void updateOrderPaymentTest() {
		org.minnal.examples.oms.domain.Order order = createDomain(org.minnal.examples.oms.domain.Order.class);
		order.persist();
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		order.collection("payments").add(payment);
		order.persist();
		org.minnal.examples.oms.domain.Payment modifiedpayment = createDomain(
				org.minnal.examples.oms.domain.Payment.class, 1);
		ContainerResponse response = call(request("/orders/"
				+ order.getId() + "/payments/"
				+ payment.getId(), HttpMethod.PUT,
				serialize(modifiedpayment)));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_NO_CONTENT);
		payment.merge();
		assertTrue(compare(modifiedpayment, payment, 1));
	}

	@Test
	public void deleteOrderPaymentTest() {
		org.minnal.examples.oms.domain.Order order = createDomain(org.minnal.examples.oms.domain.Order.class);
		order.persist();
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		order.collection("payments").add(payment);
		order.persist();
		ContainerResponse response = call(request("/orders/"
				+ order.getId() + "/payments/"
				+ payment.getId(), HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_NO_CONTENT);
		response = call(request("/orders/" + order.getId()
				+ "/payments/" + payment.getId(),
				HttpMethod.GET, serialize(payment)));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_NOT_FOUND);
	}

	@Test
	public void listOrderOrderItemTest() {
		org.minnal.examples.oms.domain.Order order = createDomain(org.minnal.examples.oms.domain.Order.class);
		order.persist();

		org.minnal.examples.oms.domain.OrderItem orderItem = createDomain(org.minnal.examples.oms.domain.OrderItem.class);
		order.collection("orderItems").add(orderItem);
		order.persist();

		ContainerResponse response = call(request("/orders/"
				+ order.getId() + "/order_items/",
				HttpMethod.GET));
		assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
		assertEquals(deserializeCollection(
				getByteBufferFromContainerResp(response),
				java.util.List.class,
				org.minnal.examples.oms.domain.OrderItem.class)
				.size(), order.getOrderItems().size());
	}

	@Test
	public void readOrderOrderItemTest() {
		org.minnal.examples.oms.domain.Order order = createDomain(org.minnal.examples.oms.domain.Order.class);
		order.persist();
		org.minnal.examples.oms.domain.OrderItem orderItem = createDomain(org.minnal.examples.oms.domain.OrderItem.class);
		order.collection("orderItems").add(orderItem);
		order.persist();
		ContainerResponse response = call(request("/orders/"
				+ order.getId() + "/order_items/"
				+ orderItem.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
		assertEquals(deserialize(
				getByteBufferFromContainerResp(response),
				org.minnal.examples.oms.domain.OrderItem.class)
				.getId(), orderItem.getId());
	}

	@Test
	public void createOrderOrderItemTest() {
		org.minnal.examples.oms.domain.Order order = createDomain(org.minnal.examples.oms.domain.Order.class);
		order.persist();
		org.minnal.examples.oms.domain.OrderItem orderItem = createDomain(org.minnal.examples.oms.domain.OrderItem.class);
		ContainerResponse response = call(request("/orders/"
				+ order.getId() + "/order_items/",
				HttpMethod.POST, serialize(orderItem)));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_CREATED);
	}

	@Test
	public void updateOrderOrderItemTest() {
		org.minnal.examples.oms.domain.Order order = createDomain(org.minnal.examples.oms.domain.Order.class);
		order.persist();
		org.minnal.examples.oms.domain.OrderItem orderItem = createDomain(org.minnal.examples.oms.domain.OrderItem.class);
		order.collection("orderItems").add(orderItem);
		order.persist();
		org.minnal.examples.oms.domain.OrderItem modifiedorderItem = createDomain(
				org.minnal.examples.oms.domain.OrderItem.class,
				1);
		ContainerResponse response = call(request("/orders/"
				+ order.getId() + "/order_items/"
				+ orderItem.getId(), HttpMethod.PUT,
				serialize(modifiedorderItem)));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_NO_CONTENT);
		orderItem.merge();
		assertTrue(compare(modifiedorderItem, orderItem, 1));
	}

	@Test
	public void deleteOrderOrderItemTest() {
		org.minnal.examples.oms.domain.Order order = createDomain(org.minnal.examples.oms.domain.Order.class);
		order.persist();
		org.minnal.examples.oms.domain.OrderItem orderItem = createDomain(org.minnal.examples.oms.domain.OrderItem.class);
		order.collection("orderItems").add(orderItem);
		order.persist();
		ContainerResponse response = call(request("/orders/"
				+ order.getId() + "/order_items/"
				+ orderItem.getId(), HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_NO_CONTENT);
		response = call(request("/orders/" + order.getId()
				+ "/order_items/" + orderItem.getId(),
				HttpMethod.GET, serialize(orderItem)));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_NOT_FOUND);
	}

}