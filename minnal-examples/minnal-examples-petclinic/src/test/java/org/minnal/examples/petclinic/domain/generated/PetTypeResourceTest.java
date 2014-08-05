package org.minnal.examples.petclinic.domain.generated;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import io.netty.handler.codec.http.HttpResponseStatus;

import org.minnal.core.resource.BaseJPAResourceTest;
import org.minnal.core.serializer.Serializer;
import org.testng.annotations.Test;

/**
 * This is an auto generated test class by minnal-generator
 */
public class PetTypeResourceTest extends BaseJPAResourceTest {
	@Test
	public void listPetTypeTest() {
		org.minnal.examples.petclinic.domain.PetType petType = createDomain(org.minnal.examples.petclinic.domain.PetType.class);
		petType.persist();
		Response response = call(request("/pet_types/", HttpMethod.GET));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(serializer
				.deserializeCollection(
						response.getContent(),
						java.util.List.class,
						org.minnal.examples.petclinic.domain.PetType.class)
				.size(),
				(int) org.minnal.examples.petclinic.domain.PetType
						.count());
	}

	@Test
	public void readPetTypeTest() {
		org.minnal.examples.petclinic.domain.PetType petType = createDomain(org.minnal.examples.petclinic.domain.PetType.class);
		petType.persist();
		Response response = call(request(
				"/pet_types/" + petType.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(serializer
				.deserialize(response.getContent(),
						org.minnal.examples.petclinic.domain.PetType.class)
				.getId(), petType.getId());
	}

	@Test
	public void createPetTypeTest() {
		org.minnal.examples.petclinic.domain.PetType petType = createDomain(org.minnal.examples.petclinic.domain.PetType.class);
		Response response = call(request("/pet_types/",
				HttpMethod.POST,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(petType)));
		assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
	}

	@Test
	public void updatePetTypeTest() {
		org.minnal.examples.petclinic.domain.PetType petType = createDomain(org.minnal.examples.petclinic.domain.PetType.class);
		petType.persist();
		org.minnal.examples.petclinic.domain.PetType modifiedpetType = createDomain(
				org.minnal.examples.petclinic.domain.PetType.class,
				1);
		Response response = call(request(
				"/pet_types/" + petType.getId(),
				HttpMethod.PUT,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(modifiedpetType)));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		petType.merge();
		assertTrue(compare(modifiedpetType, petType, 1));
	}

	@Test
	public void deletePetTypeTest() {
		org.minnal.examples.petclinic.domain.PetType petType = createDomain(org.minnal.examples.petclinic.domain.PetType.class);
		petType.persist();
		Response response = call(request(
				"/pet_types/" + petType.getId(),
				HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		response = call(request("/pet_types/" + petType.getId(),
				HttpMethod.GET,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(petType)));
		assertEquals(response.getStatus(), HttpResponseStatus.NOT_FOUND);
	}

}