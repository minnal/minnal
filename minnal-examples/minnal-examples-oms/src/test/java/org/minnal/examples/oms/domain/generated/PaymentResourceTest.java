package org.minnal.examples.oms.domain.generated;

import org.glassfish.jersey.server.ContainerResponse;
import org.testng.annotations.Test;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;
import static org.testng.Assert.*;

/**
 * This is an auto generated test class by minnal-generator
 */
public class PaymentResourceTest extends org.minnal.test.BaseMinnalResourceTest {
	@Test
	public void listPaymentTest() {
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		payment.persist();
		ContainerResponse response = call(request("/payments/",
				HttpMethod.GET));
		assertEquals(response.getStatus(),
				Response.Status.OK.getStatusCode());
		assertEquals(deserializeCollection(getContent(response),
				java.util.List.class,
				org.minnal.examples.oms.domain.Payment.class)
				.size(),
				(int) org.minnal.examples.oms.domain.Payment
						.count());
	}

	@Test
	public void readPaymentTest() {
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		payment.persist();
		ContainerResponse response = call(request("/payments/"
				+ payment.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(),
				Response.Status.OK.getStatusCode());
		assertEquals(deserialize(getContent(response),
				org.minnal.examples.oms.domain.Payment.class)
				.getId(), payment.getId());
	}

	@Test
	public void createPaymentTest() {
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		ContainerResponse response = call(request("/payments/",
				HttpMethod.POST, serialize(payment)));
		assertEquals(response.getStatus(),
				Response.Status.CREATED.getStatusCode());
	}

	@Test
	public void updatePaymentTest() {
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		payment.persist();
		org.minnal.examples.oms.domain.Payment modifiedpayment = createDomain(
				org.minnal.examples.oms.domain.Payment.class, 1);
		ContainerResponse response = call(request("/payments/"
				+ payment.getId(), HttpMethod.PUT,
				serialize(modifiedpayment)));
		assertEquals(response.getStatus(),
				Response.Status.NO_CONTENT.getStatusCode());
		payment.merge();
		assertTrue(compare(modifiedpayment, payment, 1));
	}

	@Test
	public void deletePaymentTest() {
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		payment.persist();
		ContainerResponse response = call(request("/payments/"
				+ payment.getId(), HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				Response.Status.NO_CONTENT.getStatusCode());
		response = call(request("/payments/" + payment.getId(),
				HttpMethod.GET, serialize(payment)));
		assertEquals(response.getStatus(),
				Response.Status.NOT_FOUND.getStatusCode());
	}

}