package org.minnal.examples.petclinic.domain.generated;

import org.glassfish.jersey.server.ContainerResponse;
import org.minnal.core.resource.BaseMinnalResourceTest;
import org.testng.annotations.Test;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;
import static org.testng.Assert.*;

/**
 * This is an auto generated test class by minnal-generator
 */
public class VetResourceTest extends BaseMinnalResourceTest {
	@Test
	public void listVetTest() {
		org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
		vet.persist();
		ContainerResponse response = call(request("/vets/",
				HttpMethod.GET));
		assertEquals(response.getStatus(),
				Response.Status.OK.getStatusCode());
		assertEquals(deserializeCollection(getContent(response),
				java.util.List.class,
				org.minnal.examples.petclinic.domain.Vet.class)
				.size(),
				(int) org.minnal.examples.petclinic.domain.Vet
						.count());
	}

	@Test
	public void readVetTest() {
		org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
		vet.persist();
		ContainerResponse response = call(request(
				"/vets/" + vet.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(),
				Response.Status.OK.getStatusCode());
		assertEquals(deserialize(getContent(response),
				org.minnal.examples.petclinic.domain.Vet.class)
				.getId(), vet.getId());
	}

	@Test
	public void createVetTest() {
		org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
		ContainerResponse response = call(request("/vets/",
				HttpMethod.POST, serialize(vet)));
		assertEquals(response.getStatus(),
				Response.Status.CREATED.getStatusCode());
	}

	@Test
	public void updateVetTest() {
		org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
		vet.persist();
		org.minnal.examples.petclinic.domain.Vet modifiedvet = createDomain(
				org.minnal.examples.petclinic.domain.Vet.class,
				1);
		ContainerResponse response = call(request(
				"/vets/" + vet.getId(), HttpMethod.PUT,
				serialize(modifiedvet)));
		assertEquals(response.getStatus(),
				Response.Status.NO_CONTENT.getStatusCode());
		vet.merge();
		assertTrue(compare(modifiedvet, vet, 1));
	}

	@Test
	public void deleteVetTest() {
		org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
		vet.persist();
		ContainerResponse response = call(request(
				"/vets/" + vet.getId(), HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				Response.Status.NO_CONTENT.getStatusCode());
		response = call(request("/vets/" + vet.getId(), HttpMethod.GET,
				serialize(vet)));
		assertEquals(response.getStatus(),
				Response.Status.NOT_FOUND.getStatusCode());
	}

	@Test
	public void listVetSpecialtyTest() {
		org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
		vet.persist();

		org.minnal.examples.petclinic.domain.Specialty specialty = createDomain(org.minnal.examples.petclinic.domain.Specialty.class);
		vet.collection("specialties").add(specialty);
		vet.persist();

		ContainerResponse response = call(request(
				"/vets/" + vet.getId() + "/specialties/",
				HttpMethod.GET));
		assertEquals(response.getStatus(),
				Response.Status.OK.getStatusCode());
		assertEquals(deserializeCollection(
				getContent(response),
				java.util.List.class,
				org.minnal.examples.petclinic.domain.Specialty.class)
				.size(), vet.getSpecialties().size());
	}

	@Test
	public void readVetSpecialtyTest() {
		org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
		vet.persist();
		org.minnal.examples.petclinic.domain.Specialty specialty = createDomain(org.minnal.examples.petclinic.domain.Specialty.class);
		vet.collection("specialties").add(specialty);
		vet.persist();
		ContainerResponse response = call(request(
				"/vets/" + vet.getId() + "/specialties/"
						+ specialty.getId(),
				HttpMethod.GET));
		assertEquals(response.getStatus(),
				Response.Status.OK.getStatusCode());
		assertEquals(deserialize(
				getContent(response),
				org.minnal.examples.petclinic.domain.Specialty.class)
				.getId(), specialty.getId());
	}

	@Test
	public void createVetSpecialtyTest() {
		org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
		vet.persist();
		org.minnal.examples.petclinic.domain.Specialty specialty = createDomain(org.minnal.examples.petclinic.domain.Specialty.class);
		ContainerResponse response = call(request(
				"/vets/" + vet.getId() + "/specialties/",
				HttpMethod.POST, serialize(specialty)));
		assertEquals(response.getStatus(),
				Response.Status.CREATED.getStatusCode());
	}

	@Test
	public void updateVetSpecialtyTest() {
		org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
		vet.persist();
		org.minnal.examples.petclinic.domain.Specialty specialty = createDomain(org.minnal.examples.petclinic.domain.Specialty.class);
		vet.collection("specialties").add(specialty);
		vet.persist();
		org.minnal.examples.petclinic.domain.Specialty modifiedspecialty = createDomain(
				org.minnal.examples.petclinic.domain.Specialty.class,
				1);
		ContainerResponse response = call(request(
				"/vets/" + vet.getId() + "/specialties/"
						+ specialty.getId(),
				HttpMethod.PUT, serialize(modifiedspecialty)));
		assertEquals(response.getStatus(),
				Response.Status.NO_CONTENT.getStatusCode());
		specialty.merge();
		assertTrue(compare(modifiedspecialty, specialty, 1));
	}

	@Test
	public void deleteVetSpecialtyTest() {
		org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
		vet.persist();
		org.minnal.examples.petclinic.domain.Specialty specialty = createDomain(org.minnal.examples.petclinic.domain.Specialty.class);
		vet.collection("specialties").add(specialty);
		vet.persist();
		ContainerResponse response = call(request(
				"/vets/" + vet.getId() + "/specialties/"
						+ specialty.getId(),
				HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				Response.Status.NO_CONTENT.getStatusCode());
		response = call(request("/vets/" + vet.getId()
				+ "/specialties/" + specialty.getId(),
				HttpMethod.GET, serialize(specialty)));
		assertEquals(response.getStatus(),
				Response.Status.NOT_FOUND.getStatusCode());
	}

}