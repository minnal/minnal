/**
 * 
 */
package org.minnal.core.route;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.minnal.core.MinnalException;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.resource.DummyResource;
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
		resource = mock(DummyResource.class);
		request = mock(Request.class);
		response = mock(Response.class);
	}
	
	@Test
	public void shouldInvokeAction() throws Exception {
		Action action = new Action(resource, resource.getClass().getDeclaredMethod("methodWithValidParameters", Request.class, Response.class));
		action.invoke(request, response);
		verify(resource).methodWithValidParameters(request, response);
	}
	
	@Test(expectedExceptions=MinnalException.class)
	public void shouldThrowExceptionWhenActionHasWrongMethod() throws Exception {
		Action action = new Action(resource, resource.getClass().getDeclaredMethod("methodWithoutParameters"));
		action.invoke(request, response);
	}
}
