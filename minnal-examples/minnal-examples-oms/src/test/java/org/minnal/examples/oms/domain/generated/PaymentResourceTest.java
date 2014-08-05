package org.minnal.examples.oms.domain.generated;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import io.netty.handler.codec.http.HttpResponseStatus;

import org.minnal.core.resource.BaseJPAResourceTest;
import org.minnal.core.serializer.Serializer;
import org.testng.annotations.Test;

/**
 * This is an auto generated test class by minnal-generator
 */
public class PaymentResourceTest extends BaseJPAResourceTest {
	@Test
	public void listPaymentTest() {
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		payment.persist();
		Response response = call(request("/payments/", HttpMethod.GET));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(
				serializer
						.deserializeCollection(
								response
										.getContent(),
								java.util.List.class,
								org.minnal.examples.oms.domain.Payment.class)
						.size(),
				(int) org.minnal.examples.oms.domain.Payment.count());
	}

	@Test
	public void createPaymentTest() {
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		Response response = call(request("/payments/", HttpMethod.POST,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(payment)));
		assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
	}

	@Test
	public void updatePaymentTest() {
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		payment.persist();
		org.minnal.examples.oms.domain.Payment modifiedpayment = createDomain(
				org.minnal.examples.oms.domain.Payment.class, 1);
		Response response = call(request(
				"/payments/" + payment.getId(), HttpMethod.PUT,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(modifiedpayment)));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		payment.merge();
		assertTrue(compare(modifiedpayment, payment, 1));
	}

	@Test
	public void readPaymentTest() {
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		payment.persist();
		Response response = call(request(
				"/payments/" + payment.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(serializer.deserialize(response.getContent(),
				org.minnal.examples.oms.domain.Payment.class)
				.getId(), payment.getId());
	}

	@Test
	public void deletePaymentTest() {
		org.minnal.examples.oms.domain.Payment payment = createDomain(org.minnal.examples.oms.domain.Payment.class);
		payment.persist();
		Response response = call(request(
				"/payments/" + payment.getId(),
				HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		response = call(request("/payments/" + payment.getId(),
				HttpMethod.GET,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(payment)));
		assertEquals(response.getStatus(), HttpResponseStatus.NOT_FOUND);
	}

}