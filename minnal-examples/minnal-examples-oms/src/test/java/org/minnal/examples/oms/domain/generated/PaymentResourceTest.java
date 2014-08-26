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
public class PaymentResourceTest extends BaseMinnalResourceTest {
	@Test
	public void listPaymentTest() {
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		payment.persist();
		ContainerResponse response = call(request("/payments/",
				HttpMethod.GET));
		assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
		assertEquals(deserializeCollection(
				getByteBufferFromContainerResp(response),
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
		assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
		assertEquals(deserialize(
				getByteBufferFromContainerResp(response),
				org.minnal.examples.oms.domain.Payment.class)
				.getId(), payment.getId());
	}

	@Test
	public void createPaymentTest() {
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		ContainerResponse response = call(request("/payments/",
				HttpMethod.POST, serialize(payment)));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_CREATED);
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
				HttpServletResponse.SC_NO_CONTENT);
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
				HttpServletResponse.SC_NO_CONTENT);
		response = call(request("/payments/" + payment.getId(),
				HttpMethod.GET, serialize(payment)));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_NOT_FOUND);
	}

}