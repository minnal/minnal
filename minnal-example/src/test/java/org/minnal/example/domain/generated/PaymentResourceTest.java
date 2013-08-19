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
public class PaymentResourceTest extends BaseJPAResourceTest {

	@Test
	public void createPaymentTest() {
		org.minnal.example.domain.Payment payment = createDomain(org.minnal.example.domain.Payment.class);
		Response response = call(request("/payments/", HttpMethod.POST,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(payment)));
		assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
	}

	@Test
	public void readPaymentTest() {
		org.minnal.example.domain.Payment payment = createDomain(org.minnal.example.domain.Payment.class);
		payment.persist();
		Response response = call(request(
				"/payments/" + payment.getId(), HttpMethod.GET,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(payment)));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(serializer.deserialize(response.getContent(),
				org.minnal.example.domain.Payment.class)
				.getId(), payment.getId());
	}

	@Test
	public void deletePaymentTest() {
		org.minnal.example.domain.Payment payment = createDomain(org.minnal.example.domain.Payment.class);
		payment.persist();
		Response response = call(request(
				"/payments/" + payment.getId(),
				HttpMethod.DELETE,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(payment)));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		assertFalse(org.minnal.example.domain.Payment.exists(payment
				.getId()));
	}

}