package org.minnal.examples.petclinic.domain.generated;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.glassfish.jersey.server.ContainerResponse;
import org.minnal.core.resource.BaseMinnalResourceTest;
import org.minnal.examples.petclinic.domain.Specialty;
import org.minnal.examples.petclinic.domain.Vet;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * This is an auto generated test class by minnal-generator
 */
public class VetResourceTest extends BaseMinnalResourceTest {
    @Test
    public void listVetTest() {
        org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
        vet.persist();
        ContainerResponse response = call(request("/vets/", HttpMethod.GET.name()));
        assertEquals(response.getStatus(), HttpResponseStatus.OK.code());
        assertEquals(((List<Vet>) response.getEntity()).size(), (int) org.minnal.examples.petclinic.domain.Vet.count());
    }

    @Test
    public void readVetTest() {
        org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
        vet.persist();
        ContainerResponse response = call(request("/vets/" + vet.getId(), HttpMethod.GET.name()));

        assertEquals(response.getStatus(), HttpResponseStatus.OK.code());
        assertEquals(((Vet) response.getEntity()).getId(), vet.getId());
    }

    @Test
    public void createVetTest() throws IOException {
        org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
        ContainerResponse response = call(request("/vets", HttpMethod.POST.name(), serialize(vet)));
        assertEquals(response.getStatus(), HttpResponseStatus.CREATED.code());
        System.out.println(response.getEntityStream().getClass());
    }

    @Test
    public void updateVetTest() {
        org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
        vet.persist();
        org.minnal.examples.petclinic.domain.Vet modifiedvet = createDomain(org.minnal.examples.petclinic.domain.Vet.class, 1);
        ContainerResponse response = call(request("/vets/" + vet.getId(), HttpMethod.PUT.name(), serialize(modifiedvet)));
        assertEquals(response.getStatus(), HttpResponseStatus.NO_CONTENT.code());
        vet.merge();
        assertTrue(compare(modifiedvet, vet, 1));
    }

    @Test
    public void deleteVetTest() {
        org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
        vet.persist();
        ContainerResponse response = call(request("/vets/" + vet.getId(), HttpMethod.DELETE.name()));
        assertEquals(response.getStatus(), HttpResponseStatus.NO_CONTENT.code());
        response = call(request("/vets/" + vet.getId(), HttpMethod.GET.name(), serialize(vet)));
        assertEquals(response.getStatus(), HttpResponseStatus.NOT_FOUND.code());
    }

    @Test
    public void listVetSpecialtyTest() {
        org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
        vet.persist();

        org.minnal.examples.petclinic.domain.Specialty specialty = createDomain(org.minnal.examples.petclinic.domain.Specialty.class);
        vet.collection("specialties").add(specialty);
        vet.persist();

        ContainerResponse response = call(request("/vets/" + vet.getId() + "/specialties/", HttpMethod.GET.name()));
        assertEquals(response.getStatus(), HttpResponseStatus.OK.code());
        assertEquals(((List<Vet>) response.getEntity()).size(), vet.getSpecialties().size());
    }

    @Test
    public void readVetSpecialtyTest() {
        org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
        vet.persist();
        org.minnal.examples.petclinic.domain.Specialty specialty = createDomain(org.minnal.examples.petclinic.domain.Specialty.class);
        vet.collection("specialties").add(specialty);
        vet.persist();
        ContainerResponse response = call(request("/vets/" + vet.getId() + "/specialties/" + specialty.getId(), HttpMethod.GET.name()));
        assertEquals(response.getStatus(), HttpResponseStatus.OK.code());
        assertEquals(((Specialty) response.getEntity()).getId(), specialty.getId());
    }

    @Test
    public void createVetSpecialtyTest() {
        org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
        vet.persist();
        org.minnal.examples.petclinic.domain.Specialty specialty = createDomain(org.minnal.examples.petclinic.domain.Specialty.class);
        ContainerResponse response = call(request("/vets/" + vet.getId() + "/specialties/", HttpMethod.POST.name(), serialize(specialty)));
        assertEquals(response.getStatus(), HttpResponseStatus.CREATED.code());
    }

    @Test
    public void updateVetSpecialtyTest() {
        org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
        vet.persist();
        org.minnal.examples.petclinic.domain.Specialty specialty = createDomain(org.minnal.examples.petclinic.domain.Specialty.class);
        vet.collection("specialties").add(specialty);
        vet.persist();
        org.minnal.examples.petclinic.domain.Specialty modifiedspecialty = createDomain(org.minnal.examples.petclinic.domain.Specialty.class, 1);
        ContainerResponse response = call(request("/vets/" + vet.getId() + "/specialties/" + specialty.getId(), HttpMethod.PUT.name(), serialize(modifiedspecialty)));
        assertEquals(response.getStatus(), HttpResponseStatus.NO_CONTENT.code());
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
		ContainerResponse response = call(request("/vets/" + vet.getId() + "/specialties/" + specialty.getId(), HttpMethod.DELETE.name()));
		assertEquals(response.getStatus(),HttpResponseStatus.NO_CONTENT.code());
		response = call(request("/vets/" + vet.getId()+ "/specialties/" + specialty.getId(),HttpMethod.GET.name(), serialize(specialty)));
		assertEquals(response.getStatus(), HttpResponseStatus.NOT_FOUND.code());
	}

}