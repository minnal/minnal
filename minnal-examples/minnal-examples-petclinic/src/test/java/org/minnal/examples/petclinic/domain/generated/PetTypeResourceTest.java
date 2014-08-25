package org.minnal.examples.petclinic.domain.generated;

import org.glassfish.jersey.server.ContainerResponse;
import org.minnal.core.resource.BaseMinnalResourceTest;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;

import static org.testng.Assert.*;

/**
 * This is an auto generated test class by minnal-generator
 */
public class PetTypeResourceTest extends BaseMinnalResourceTest {
	@Test
	public void listPetTypeTest() {
		org.minnal.examples.petclinic.domain.PetType petType = createDomain(org.minnal.examples.petclinic.domain.PetType.class);
		petType.persist();
		ContainerResponse response = call(request("/pet_types/",
				HttpMethod.GET));
		assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
		assertEquals(deserializeCollection(
				getByteBufferFromContainerResp(response),
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
		ContainerResponse response = call(request("/pet_types/"
				+ petType.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(), HttpServletResponse.SC_OK);
		assertEquals(deserialize(
				getByteBufferFromContainerResp(response),
				org.minnal.examples.petclinic.domain.PetType.class)
				.getId(), petType.getId());
	}

	@Test
	public void createPetTypeTest() {
		org.minnal.examples.petclinic.domain.PetType petType = createDomain(org.minnal.examples.petclinic.domain.PetType.class);
		ContainerResponse response = call(request("/pet_types/",
				HttpMethod.POST, serialize(petType)));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_CREATED);
	}

	@Test
	public void updatePetTypeTest() {
		org.minnal.examples.petclinic.domain.PetType petType = createDomain(org.minnal.examples.petclinic.domain.PetType.class);
		petType.persist();
		org.minnal.examples.petclinic.domain.PetType modifiedpetType = createDomain(
				org.minnal.examples.petclinic.domain.PetType.class,
				1);
		ContainerResponse response = call(request("/pet_types/"
				+ petType.getId(), HttpMethod.PUT,
				serialize(modifiedpetType)));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_NO_CONTENT);
		petType.merge();
		assertTrue(compare(modifiedpetType, petType, 1));
	}

	@Test
	public void deletePetTypeTest() {
		org.minnal.examples.petclinic.domain.PetType petType = createDomain(org.minnal.examples.petclinic.domain.PetType.class);
		petType.persist();
		ContainerResponse response = call(request("/pet_types/"
				+ petType.getId(), HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_NO_CONTENT);
		response = call(request("/pet_types/" + petType.getId(),
				HttpMethod.GET, serialize(petType)));
		assertEquals(response.getStatus(),
				HttpServletResponse.SC_NOT_FOUND);
	}

}