package org.minnal.examples.petclinic.domain.generated;

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
public class VetResourceTest extends BaseJPAResourceTest {
    @Test
    public void listVetTest() {
        org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
        vet.persist();
        FullHttpResponse response = call(request("/vets/",
                HttpMethod.GET));
        assertEquals(response.getStatus(), HttpResponseStatus.OK);
        assertEquals(deserializeCollection(response.content(),
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
        FullHttpResponse response = call(request(
                "/vets/" + vet.getId(), HttpMethod.GET));
        assertEquals(response.getStatus(), HttpResponseStatus.OK);
        assertEquals(deserialize(response.content(),
                org.minnal.examples.petclinic.domain.Vet.class)
                .getId(), vet.getId());
    }

    @Test
    public void createVetTest() {
        org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
        FullHttpResponse response = call(request("/vets/",
                HttpMethod.POST, serialize(vet)));
        assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
    }

    @Test
    public void updateVetTest() {
        org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
        vet.persist();
        org.minnal.examples.petclinic.domain.Vet modifiedvet = createDomain(
                org.minnal.examples.petclinic.domain.Vet.class,
                1);
        FullHttpResponse response = call(request(
                "/vets/" + vet.getId(), HttpMethod.PUT,
                serialize(modifiedvet)));
        assertEquals(response.getStatus(),
                HttpResponseStatus.NO_CONTENT);
        vet.merge();
        assertTrue(compare(modifiedvet, vet, 1));
    }

    @Test
    public void deleteVetTest() {
        org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
        vet.persist();
        FullHttpResponse response = call(request(
                "/vets/" + vet.getId(), HttpMethod.DELETE));
        assertEquals(response.getStatus(),
                HttpResponseStatus.NO_CONTENT);
        response = call(request("/vets/" + vet.getId(), HttpMethod.GET,
                serialize(vet)));
        assertEquals(response.getStatus(), HttpResponseStatus.NOT_FOUND);
    }

    @Test
    public void listVetSpecialtyTest() {
        org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
        vet.persist();

        org.minnal.examples.petclinic.domain.Specialty specialty = createDomain(org.minnal.examples.petclinic.domain.Specialty.class);
        vet.collection("specialties").add(specialty);
        vet.persist();

        FullHttpResponse response = call(request("/vets/" + vet.getId()
                + "/specialties/", HttpMethod.GET));
        assertEquals(response.getStatus(), HttpResponseStatus.OK);
        assertEquals(deserializeCollection(
                response.content(),
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
        FullHttpResponse response = call(request("/vets/" + vet.getId()
                        + "/specialties/" + specialty.getId(),
                HttpMethod.GET));
        assertEquals(response.getStatus(), HttpResponseStatus.OK);
        assertEquals(deserialize(
                response.content(),
                org.minnal.examples.petclinic.domain.Specialty.class)
                .getId(), specialty.getId());
    }

    @Test
    public void createVetSpecialtyTest() {
        org.minnal.examples.petclinic.domain.Vet vet = createDomain(org.minnal.examples.petclinic.domain.Vet.class);
        vet.persist();
        org.minnal.examples.petclinic.domain.Specialty specialty = createDomain(org.minnal.examples.petclinic.domain.Specialty.class);
        FullHttpResponse response = call(request("/vets/" + vet.getId()
                        + "/specialties/", HttpMethod.POST,
                serialize(specialty)));
        assertEquals(response.getStatus(), HttpResponseStatus.CREATED);
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
        FullHttpResponse response = call(request("/vets/" + vet.getId()
                        + "/specialties/" + specialty.getId(),
                HttpMethod.PUT, serialize(modifiedspecialty)));
        assertEquals(response.getStatus(),
                HttpResponseStatus.NO_CONTENT);
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
        FullHttpResponse response = call(request("/vets/" + vet.getId()
                        + "/specialties/" + specialty.getId(),
                HttpMethod.DELETE));
        assertEquals(response.getStatus(),
                HttpResponseStatus.NO_CONTENT);
        response = call(request("/vets/" + vet.getId()
                        + "/specialties/" + specialty.getId(),
                HttpMethod.GET, serialize(specialty)));
        assertEquals(response.getStatus(), HttpResponseStatus.NOT_FOUND);
    }

}