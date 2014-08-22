//package org.minnal.examples.petclinic.domain.generated;
//
//import static org.testng.Assert.assertEquals;
//import static org.testng.Assert.assertTrue;
//import io.netty.handler.codec.http.FullHttpResponse;
//import io.netty.handler.codec.http.HttpMethod;
//import io.netty.handler.codec.http.HttpResponseStatus;
//
//import org.minnal.core.resource.BaseJPAResourceTest;
//import org.testng.annotations.Test;
//
///**
// * This is an auto generated test class by minnal-generator
// */
//public class PetTypeResourceTest extends BaseJPAResourceTest {
//	@Test
//	public void listPetTypeTest() {
//		org.minnal.examples.petclinic.domain.PetType petType = createDomain(org.minnal.examples.petclinic.domain.PetType.class);
//		petType.persist();
//		FullHttpResponse response = call(request("/pet_types/",
//				HttpMethod.GET));
//		assertEquals(response.getStatus(), HttpResponseStatus.OK);
//		assertEquals(deserializeCollection(
//				response.content(),
//				java.util.List.class,
//				org.minnal.examples.petclinic.domain.PetType.class)
//				.size(),
//				(int) org.minnal.examples.petclinic.domain.PetType
//						.count());
//	}
//
//	@Test
//	public void readPetTypeTest() {
//		org.minnal.examples.petclinic.domain.PetType petType = createDomain(org.minnal.examples.petclinic.domain.PetType.class);
//		petType.persist();
//		FullHttpResponse response = call(request("/pet_types/"
//				+ petType.getId(), HttpMethod.GET));
//		assertEquals(response.getStatus(), HttpResponseStatus.OK);
//		assertEquals(deserialize(
//				response.content(),
//				org.minnal.examples.petclinic.domain.PetType.class)
//				.getId(), petType.getId());
//	}
//
//	@Test
//	public void createPetTypeTest() {
//		org.minnal.examples.petclinic.domain.PetType petType = createDomain(org.minnal.examples.petclinic.domain.PetType.class);
//		FullHttpResponse response = call(request("/pet_types/",
//				HttpMethod.POST, serialize(petType)));
//		assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
//	}
//
//	@Test
//	public void updatePetTypeTest() {
//		org.minnal.examples.petclinic.domain.PetType petType = createDomain(org.minnal.examples.petclinic.domain.PetType.class);
//		petType.persist();
//		org.minnal.examples.petclinic.domain.PetType modifiedpetType = createDomain(
//				org.minnal.examples.petclinic.domain.PetType.class,
//				1);
//		FullHttpResponse response = call(request("/pet_types/"
//				+ petType.getId(), HttpMethod.PUT,
//				serialize(modifiedpetType)));
//		assertEquals(response.getStatus(),
//				HttpResponseStatus.NO_CONTENT);
//		petType.merge();
//		assertTrue(compare(modifiedpetType, petType, 1));
//	}
//
//	@Test
//	public void deletePetTypeTest() {
//		org.minnal.examples.petclinic.domain.PetType petType = createDomain(org.minnal.examples.petclinic.domain.PetType.class);
//		petType.persist();
//		FullHttpResponse response = call(request("/pet_types/"
//				+ petType.getId(), HttpMethod.DELETE));
//		assertEquals(response.getStatus(),
//				HttpResponseStatus.NO_CONTENT);
//		response = call(request("/pet_types/" + petType.getId(),
//				HttpMethod.GET, serialize(petType)));
//		assertEquals(response.getStatus(), HttpResponseStatus.NOT_FOUND);
//	}
//
//}