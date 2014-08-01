/**
 * 
 */
package org.minnal.instrument.resource.metadata.handler;

import static org.testng.Assert.assertEquals;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.minnal.instrument.metadata.handler.AbstractAnnotationHandler;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class AbstractResourceAnnotationHandlerTest {

	@Test
	public void shouldGetPathAnnotationHandler() throws Exception {
		final class c { @Path("/path") public void someMethod() {} }
		Path action = c.class.getDeclaredMethod("someMethod").getAnnotation(Path.class);
		assertEquals(AbstractAnnotationHandler.handlerFor(action).getClass(), PathAnnotationHandler.class);
	}
	
	@Test
	public void shouldGetGETAnnotationHandler() throws Exception {
		final class c { @GET public void someMethod() {} }
		GET action = c.class.getDeclaredMethod("someMethod").getAnnotation(GET.class);
		assertEquals(AbstractAnnotationHandler.handlerFor(action).getClass(), GETAnnotationHandler.class);
	}

	@Test
	public void shouldGetPOSTAnnotationHandler() throws Exception {
		final class c { @POST public void someMethod() {} }
		POST action = c.class.getDeclaredMethod("someMethod").getAnnotation(POST.class);
		assertEquals(AbstractAnnotationHandler.handlerFor(action).getClass(), POSTAnnotationHandler.class);
	}
	
	@Test
	public void shouldGetPUTAnnotationHandler() throws Exception {
		final class c { @PUT public void someMethod() {} }
		PUT action = c.class.getDeclaredMethod("someMethod").getAnnotation(PUT.class);
		assertEquals(AbstractAnnotationHandler.handlerFor(action).getClass(), PUTAnnotationHandler.class);
	}
	
	@Test
	public void shouldGetDELETEAnnotationHandler() throws Exception {
		final class c { @DELETE public void someMethod() {} }
		DELETE action = c.class.getDeclaredMethod("someMethod").getAnnotation(DELETE.class);
		assertEquals(AbstractAnnotationHandler.handlerFor(action).getClass(), DELETEAnnotationHandler.class);
	}
	
	@Test
	public void shouldGetHEADAnnotationHandler() throws Exception {
		final class c { @HEAD public void someMethod() {} }
		HEAD action = c.class.getDeclaredMethod("someMethod").getAnnotation(HEAD.class);
		assertEquals(AbstractAnnotationHandler.handlerFor(action).getClass(), HEADAnnotationHandler.class);
	}
	
	@Test
	public void shouldGetOPTIONSAnnotationHandler() throws Exception {
		final class c { @OPTIONS public void someMethod() {} }
		OPTIONS action = c.class.getDeclaredMethod("someMethod").getAnnotation(OPTIONS.class);
		assertEquals(AbstractAnnotationHandler.handlerFor(action).getClass(), OPTIONSAnnotationHandler.class);
	}
}
