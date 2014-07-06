/**
 * 
 */
package org.minnal.core.route;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.minnal.core.MinnalException;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.server.exception.BadRequestException;
import org.minnal.core.server.exception.InternalServerErrorException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ActionTest {
	
	private DummyResource resource;
	
	private Request request;
	
	private Response response;
	
	@BeforeMethod
	public void setup() {
		resource = new DummyResource();
		request = mock(Request.class);
		response = mock(Response.class);
	}
	
	@Test
	public void shouldInvokeAction() throws Exception {
		resource = mock(DummyResource.class);
		Action action = new Action(resource, resource.getClass().getDeclaredMethod("methodWithValidParameters", Request.class, Response.class));
		action.invoke(request, response);
		verify(resource).methodWithValidParameters(request, response);
	}
	
	@Test(expectedExceptions=MinnalException.class)
	public void shouldThrowExceptionWhenActionHasWrongMethod() throws Exception {
		Action action = new Action(resource, resource.getClass().getDeclaredMethod("methodWithoutParameters"));
		action.invoke(request, response);
	}
	
	@Test(expectedExceptions=BadRequestException.class)
	public void shouldThrowApplicationExceptionWhenInvokationFailsDueToApplicationException() throws Exception {
		Action action = new Action(resource, resource.getClass().getDeclaredMethod("methodThrowingApplicationException", Request.class, Response.class));
		action.invoke(request, response);
	}
	
	@Test(expectedExceptions=IllegalStateException.class)
	public void shouldThrowRuntimeExceptionWhenInvokationFailsDueToRuntimeException() throws Exception {
		Action action = new Action(resource, resource.getClass().getDeclaredMethod("methodThrowingRuntiemException", Request.class, Response.class));
		action.invoke(request, response);
	}
	
	@Test(expectedExceptions=InternalServerErrorException.class)
	public void shouldThrowInternalServerErrorWhenInvokationFailsDueToCheckedException() throws Exception {
		Action action = new Action(resource, resource.getClass().getDeclaredMethod("methodThrowingCheckedException", Request.class, Response.class));
		action.invoke(request, response);
	}
	
	public static class DummyResource {
		
		public void methodThrowingApplicationException(Request request, Response response) {
			throw new BadRequestException();
		}
		
		public void methodThrowingRuntiemException(Request request, Response response) {
			throw new IllegalStateException();
		}
		
		public void methodThrowingCheckedException(Request request, Response response) throws Exception {
			throw new Exception();
		}
		
		public void methodWithoutParameters() {
			
		}
		
		public void methodWithValidParameters(Request request, Response response) {}
	}
}
