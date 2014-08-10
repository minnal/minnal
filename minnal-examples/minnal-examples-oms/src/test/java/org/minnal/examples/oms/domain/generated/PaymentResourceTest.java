package org.minnal.examples.oms.domain.generated;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;

import org.minnal.core.resource.BaseJPAResourceTest;
import org.testng.annotations.Test;

/**
 * This is an auto generated test class by minnal-generator
 */
public class PaymentResourceTest extends BaseJPAResourceTest {
	@Test
	public void listPaymentTest() {
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		payment.persist();
		FullHttpResponse response = call(request("/payments/",
				HttpMethod.GET));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(deserializeCollection(response.content(),
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
		FullHttpResponse response = call(request(
				"/payments/" + payment.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(deserialize(response.content(),
				org.minnal.examples.oms.domain.Payment.class)
				.getId(), payment.getId());
	}

	@Test
	public void createPaymentTest() {
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		FullHttpResponse response = call(request("/payments/",
				HttpMethod.POST, serialize(payment)));
		assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
	}

	@Test
	public void updatePaymentTest() {
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		payment.persist();
		org.minnal.examples.oms.domain.Payment modifiedpayment = createDomain(
				org.minnal.examples.oms.domain.Payment.class, 1);
		FullHttpResponse response = call(request(
				"/payments/" + payment.getId(), HttpMethod.PUT,
				serialize(modifiedpayment)));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		payment.merge();
		assertTrue(compare(modifiedpayment, payment, 1));
	}

	@Test
	public void deletePaymentTest() {
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		payment.persist();
		FullHttpResponse response = call(request(
				"/payments/" + payment.getId(),
				HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		response = call(request("/payments/" + payment.getId(),
				HttpMethod.GET, serialize(payment)));
		assertEquals(response.getStatus(), HttpResponseStatus.NOT_FOUND);
	}

}