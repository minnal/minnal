package org.minnal.examples.petclinic.domain.generated;

import org.glassfish.jersey.server.ContainerResponse;
import org.testng.annotations.Test;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;
import static org.testng.Assert.*;

/**
 * This is an auto generated test class by minnal-generator
 */
public class OwnerResourceTest extends org.minnal.test.BaseMinnalResourceTest {
	@Test
	public void listOwnerTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();
		ContainerResponse response = call(request("/owners/",
				HttpMethod.GET));
		assertEquals(response.getStatus(),
				Response.Status.OK.getStatusCode());
		assertEquals(deserializeCollection(
				getContent(response),
				java.util.List.class,
				org.minnal.examples.petclinic.domain.Owner.class)
				.size(),
				(int) org.minnal.examples.petclinic.domain.Owner
						.count());
	}

	@Test
	public void readOwnerTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();
		ContainerResponse response = call(request(
				"/owners/" + owner.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(),
				Response.Status.OK.getStatusCode());
		assertEquals(deserialize(
				getContent(response),
				org.minnal.examples.petclinic.domain.Owner.class)
				.getId(), owner.getId());
	}

	@Test
	public void createOwnerTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		ContainerResponse response = call(request("/owners/",
				HttpMethod.POST, serialize(owner)));
		assertEquals(response.getStatus(),
				Response.Status.CREATED.getStatusCode());
	}

	@Test
	public void updateOwnerTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();
		org.minnal.examples.petclinic.domain.Owner modifiedowner = createDomain(
				org.minnal.examples.petclinic.domain.Owner.class,
				1);
		ContainerResponse response = call(request(
				"/owners/" + owner.getId(), HttpMethod.PUT,
				serialize(modifiedowner)));
		assertEquals(response.getStatus(),
				Response.Status.NO_CONTENT.getStatusCode());
		owner.merge();
		assertTrue(compare(modifiedowner, owner, 1));
	}

	@Test
	public void deleteOwnerTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();
		ContainerResponse response = call(request(
				"/owners/" + owner.getId(), HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				Response.Status.NO_CONTENT.getStatusCode());
		response = call(request("/owners/" + owner.getId(),
				HttpMethod.GET, serialize(owner)));
		assertEquals(response.getStatus(),
				Response.Status.NOT_FOUND.getStatusCode());
	}

	@Test
	public void listOwnerPetTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();

		org.minnal.examples.petclinic.domain.Pet pet = createDomain(org.minnal.examples.petclinic.domain.Pet.class);
		owner.collection("pets").add(pet);
		owner.persist();

		ContainerResponse response = call(request(
				"/owners/" + owner.getId() + "/pets/",
				HttpMethod.GET));
		assertEquals(response.getStatus(),
				Response.Status.OK.getStatusCode());
		assertEquals(deserializeCollection(getContent(response),
				java.util.List.class,
				org.minnal.examples.petclinic.domain.Pet.class)
				.size(), owner.getPets().size());
	}

	@Test
	public void readOwnerPetTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();
		org.minnal.examples.petclinic.domain.Pet pet = createDomain(org.minnal.examples.petclinic.domain.Pet.class);
		owner.collection("pets").add(pet);
		owner.persist();
		ContainerResponse response = call(request(
				"/owners/" + owner.getId() + "/pets/"
						+ pet.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(),
				Response.Status.OK.getStatusCode());
		assertEquals(deserialize(getContent(response),
				org.minnal.examples.petclinic.domain.Pet.class)
				.getId(), pet.getId());
	}

	@Test
	public void createOwnerPetTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();
		org.minnal.examples.petclinic.domain.Pet pet = createDomain(org.minnal.examples.petclinic.domain.Pet.class);
		ContainerResponse response = call(request(
				"/owners/" + owner.getId() + "/pets/",
				HttpMethod.POST, serialize(pet)));
		assertEquals(response.getStatus(),
				Response.Status.CREATED.getStatusCode());
	}

	@Test
	public void updateOwnerPetTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();
		org.minnal.examples.petclinic.domain.Pet pet = createDomain(org.minnal.examples.petclinic.domain.Pet.class);
		owner.collection("pets").add(pet);
		owner.persist();
		org.minnal.examples.petclinic.domain.Pet modifiedpet = createDomain(
				org.minnal.examples.petclinic.domain.Pet.class,
				1);
		ContainerResponse response = call(request(
				"/owners/" + owner.getId() + "/pets/"
						+ pet.getId(), HttpMethod.PUT,
				serialize(modifiedpet)));
		assertEquals(response.getStatus(),
				Response.Status.NO_CONTENT.getStatusCode());
		pet.merge();
		assertTrue(compare(modifiedpet, pet, 1));
	}

	@Test
	public void deleteOwnerPetTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();
		org.minnal.examples.petclinic.domain.Pet pet = createDomain(org.minnal.examples.petclinic.domain.Pet.class);
		owner.collection("pets").add(pet);
		owner.persist();
		ContainerResponse response = call(request(
				"/owners/" + owner.getId() + "/pets/"
						+ pet.getId(),
				HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				Response.Status.NO_CONTENT.getStatusCode());
		response = call(request("/owners/" + owner.getId() + "/pets/"
				+ pet.getId(), HttpMethod.GET, serialize(pet)));
		assertEquals(response.getStatus(),
				Response.Status.NOT_FOUND.getStatusCode());
	}

	@Test
	public void listOwnerPetVisitTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();

		org.minnal.examples.petclinic.domain.Pet pet = createDomain(org.minnal.examples.petclinic.domain.Pet.class);
		owner.collection("pets").add(pet);
		owner.persist();

		org.minnal.examples.petclinic.domain.Visit visit = createDomain(org.minnal.examples.petclinic.domain.Visit.class);
		pet.collection("visits").add(visit);
		pet.persist();

		ContainerResponse response = call(request(
				"/owners/" + owner.getId() + "/pets/"
						+ pet.getId() + "/visits/",
				HttpMethod.GET));
		assertEquals(response.getStatus(),
				Response.Status.OK.getStatusCode());
		assertEquals(deserializeCollection(
				getContent(response),
				java.util.List.class,
				org.minnal.examples.petclinic.domain.Visit.class)
				.size(), pet.getVisits().size());
	}

	@Test
	public void readOwnerPetVisitTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();
		org.minnal.examples.petclinic.domain.Pet pet = createDomain(org.minnal.examples.petclinic.domain.Pet.class);
		owner.collection("pets").add(pet);
		owner.persist();
		org.minnal.examples.petclinic.domain.Visit visit = createDomain(org.minnal.examples.petclinic.domain.Visit.class);
		pet.collection("visits").add(visit);
		pet.persist();
		ContainerResponse response = call(request(
				"/owners/" + owner.getId() + "/pets/"
						+ pet.getId() + "/visits/"
						+ visit.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(),
				Response.Status.OK.getStatusCode());
		assertEquals(deserialize(
				getContent(response),
				org.minnal.examples.petclinic.domain.Visit.class)
				.getId(), visit.getId());
	}

	@Test
	public void createOwnerPetVisitTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();
		org.minnal.examples.petclinic.domain.Pet pet = createDomain(org.minnal.examples.petclinic.domain.Pet.class);
		owner.collection("pets").add(pet);
		owner.persist();
		org.minnal.examples.petclinic.domain.Visit visit = createDomain(org.minnal.examples.petclinic.domain.Visit.class);
		ContainerResponse response = call(request(
				"/owners/" + owner.getId() + "/pets/"
						+ pet.getId() + "/visits/",
				HttpMethod.POST, serialize(visit)));
		assertEquals(response.getStatus(),
				Response.Status.CREATED.getStatusCode());
	}

	@Test
	public void updateOwnerPetVisitTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();
		org.minnal.examples.petclinic.domain.Pet pet = createDomain(org.minnal.examples.petclinic.domain.Pet.class);
		owner.collection("pets").add(pet);
		owner.persist();
		org.minnal.examples.petclinic.domain.Visit visit = createDomain(org.minnal.examples.petclinic.domain.Visit.class);
		pet.collection("visits").add(visit);
		pet.persist();
		org.minnal.examples.petclinic.domain.Visit modifiedvisit = createDomain(
				org.minnal.examples.petclinic.domain.Visit.class,
				1);
		ContainerResponse response = call(request(
				"/owners/" + owner.getId() + "/pets/"
						+ pet.getId() + "/visits/"
						+ visit.getId(),
				HttpMethod.PUT, serialize(modifiedvisit)));
		assertEquals(response.getStatus(),
				Response.Status.NO_CONTENT.getStatusCode());
		visit.merge();
		assertTrue(compare(modifiedvisit, visit, 1));
	}

	@Test
	public void deleteOwnerPetVisitTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();
		org.minnal.examples.petclinic.domain.Pet pet = createDomain(org.minnal.examples.petclinic.domain.Pet.class);
		owner.collection("pets").add(pet);
		owner.persist();
		org.minnal.examples.petclinic.domain.Visit visit = createDomain(org.minnal.examples.petclinic.domain.Visit.class);
		pet.collection("visits").add(visit);
		pet.persist();
		ContainerResponse response = call(request(
				"/owners/" + owner.getId() + "/pets/"
						+ pet.getId() + "/visits/"
						+ visit.getId(),
				HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				Response.Status.NO_CONTENT.getStatusCode());
		response = call(request("/owners/" + owner.getId() + "/pets/"
				+ pet.getId() + "/visits/" + visit.getId(),
				HttpMethod.GET, serialize(visit)));
		assertEquals(response.getStatus(),
				Response.Status.NOT_FOUND.getStatusCode());
	}

}