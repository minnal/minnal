package org.minnal.examples.petclinic.domain.generated;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import io.netty.handler.codec.http.HttpResponseStatus;

import org.minnal.core.Response;
import org.minnal.core.resource.BaseJPAResourceTest;
import org.minnal.core.serializer.Serializer;
import org.minnal.examples.petclinic.JodatimeJsonSerializer;
import org.testng.annotations.Test;

/**
 * This is an auto generated test class by minnal-generator
 */
public class OwnerResourceTest extends BaseJPAResourceTest {
	@Test
	public void listOwnerTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();
		Response response = call(request("/owners/", HttpMethod.GET));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(serializer
				.deserializeCollection(
						response.getContent(),
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
		Response response = call(request("/owners/" + owner.getId(),
				HttpMethod.GET));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(serializer
				.deserialize(response.getContent(),
						org.minnal.examples.petclinic.domain.Owner.class)
				.getId(), owner.getId());
	}

	@Test
	public void createOwnerTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		Response response = call(request("/owners/", HttpMethod.POST,
				new JodatimeJsonSerializer().serialize(owner)));
		assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
	}

	@Test
	public void updateOwnerTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();
		org.minnal.examples.petclinic.domain.Owner modifiedowner = createDomain(
				org.minnal.examples.petclinic.domain.Owner.class,
				1);
		Response response = call(request("/owners/" + owner.getId(),
				HttpMethod.PUT,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(modifiedowner)));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		owner.merge();
		assertTrue(compare(modifiedowner, owner, 1));
	}

	@Test
	public void deleteOwnerTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();
		Response response = call(request("/owners/" + owner.getId(),
				HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		response = call(request("/owners/" + owner.getId(),
				HttpMethod.GET,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(owner)));
		assertEquals(response.getStatus(), HttpResponseStatus.NOT_FOUND);
	}

	@Test
	public void listOwnerPetTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();

		org.minnal.examples.petclinic.domain.Pet pet = createDomain(org.minnal.examples.petclinic.domain.Pet.class);
		owner.collection("pets").add(pet);
		owner.persist();

		Response response = call(request("/owners/" + owner.getId()
				+ "/pets/", HttpMethod.GET));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(serializer.deserializeCollection(
				response.getContent(), java.util.List.class,
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
		Response response = call(request("/owners/" + owner.getId()
				+ "/pets/" + pet.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(serializer.deserialize(response.getContent(),
				org.minnal.examples.petclinic.domain.Pet.class)
				.getId(), pet.getId());
	}

	@Test
	public void createOwnerPetTest() {
		org.minnal.examples.petclinic.domain.Owner owner = createDomain(org.minnal.examples.petclinic.domain.Owner.class);
		owner.persist();
		org.minnal.examples.petclinic.domain.Pet pet = createDomain(org.minnal.examples.petclinic.domain.Pet.class);
		Response response = call(request("/owners/" + owner.getId()
				+ "/pets/", HttpMethod.POST,
				new JodatimeJsonSerializer().serialize(pet)));
		assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
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
		Response response = call(request("/owners/" + owner.getId()
				+ "/pets/" + pet.getId(), HttpMethod.PUT,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(modifiedpet)));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
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
		Response response = call(request("/owners/" + owner.getId()
				+ "/pets/" + pet.getId(), HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		response = call(request("/owners/" + owner.getId() + "/pets/"
				+ pet.getId(), HttpMethod.GET,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(pet)));
		assertEquals(response.getStatus(), HttpResponseStatus.NOT_FOUND);
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

		Response response = call(request("/owners/" + owner.getId()
				+ "/pets/" + pet.getId() + "/visits/",
				HttpMethod.GET));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(serializer
				.deserializeCollection(
						response.getContent(),
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
		Response response = call(request(
				"/owners/" + owner.getId() + "/pets/"
						+ pet.getId() + "/visits/"
						+ visit.getId(), HttpMethod.GET));
		assertEquals(response.getStatus(), HttpResponseStatus.OK);
		assertEquals(serializer
				.deserialize(response.getContent(),
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
		Response response = call(request("/owners/" + owner.getId()
				+ "/pets/" + pet.getId() + "/visits/",
				HttpMethod.POST,
				new JodatimeJsonSerializer().serialize(visit)));
		assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
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
		Response response = call(request(
				"/owners/" + owner.getId() + "/pets/"
						+ pet.getId() + "/visits/"
						+ visit.getId(),
				HttpMethod.PUT,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(modifiedvisit)));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
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
		Response response = call(request(
				"/owners/" + owner.getId() + "/pets/"
						+ pet.getId() + "/visits/"
						+ visit.getId(),
				HttpMethod.DELETE));
		assertEquals(response.getStatus(),
				HttpResponseStatus.NO_CONTENT);
		response = call(request("/owners/" + owner.getId() + "/pets/"
				+ pet.getId() + "/visits/" + visit.getId(),
				HttpMethod.GET,
				Serializer.DEFAULT_JSON_SERIALIZER
						.serialize(visit)));
		assertEquals(response.getStatus(), HttpResponseStatus.NOT_FOUND);
	}

}