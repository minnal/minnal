/**
 * 
 */
package org.minnal.core;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.route.Action;
import org.minnal.core.route.Route;
import org.minnal.core.server.MessageContext;
import org.minnal.core.server.ServerRequest;
import org.minnal.core.server.ServerResponse;
import org.minnal.core.server.exception.ExceptionHandler;
import org.minnal.core.server.exception.NotFoundException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class RouterTest {
	
	private RouteResolver resolver;
	
	private Router router;
	
	private Action action;
	
	private MessageContext context;
	
	private ServerRequest request;
	
	private ServerResponse response;
	
	private Route route;
	
	@BeforeMethod
	public void setup() {
		resolver = mock(RouteResolver.class);
		request = mock(ServerRequest.class);
		response = mock(ServerResponse.class);
		when(response.getStatus()).thenReturn(HttpResponseStatus.PROCESSING);
		when(response.isContentSet()).thenReturn(false);
		router = new Router(resolver);
		context = mock(MessageContext.class);
		when(context.getRequest()).thenReturn(request);
		when(context.getResponse()).thenReturn(response);
		route = mock(Route.class);
		action = mock(Action.class);
		when(action.invoke(request, response)).thenReturn(null);
		when(route.getAction()).thenReturn(action);
		when(resolver.resolve(context)).thenReturn(route);
	}

	@Test
	public void shouldSetResponseCodeTo200IfBodyIsNotEmpty() {
		when(action.invoke(request, response)).thenReturn(Arrays.asList("test"));
		when(response.isContentSet()).thenReturn(true);
		router.route(context);
		verify(response).setStatus(HttpResponseStatus.OK);
	}
	
	@Test
	public void shouldSetResponseCodeTo204IfBodyIsEmpty() {
		router.route(context);
		verify(response).setStatus(HttpResponseStatus.NO_CONTENT);
	}
	
	@Test
	public void shouldSetContentIfNotSetAlready() {
		Object body = Arrays.asList("test");
		when(action.invoke(request, response)).thenReturn(body);
		router.route(context);
		verify(response).setContent(body);
	}
	
	@Test
	public void shouldNotSetContentIfAlreadySet() {
		Object body = Arrays.asList("test");
		when(response.isContentSet()).thenReturn(true);
		when(action.invoke(request, response)).thenReturn(body);
		router.route(context);
		verify(response, never()).setContent(body);
	}
	
	@Test
	public void shouldNotSetResponseCodeIfAlreadySet() {
		when(response.getStatus()).thenReturn(HttpResponseStatus.NOT_FOUND);
		router.route(context);
		verify(response, never()).setStatus(any(HttpResponseStatus.class));
	}
	
	@Test
	public void shouldInvokeExceptionHandlerOnException() {
		Application application = mock(Application.class);
		ExceptionHandler handler = mock(ExceptionHandler.class);
		when(application.getExceptionHandler()).thenReturn(handler);
		when(context.getApplication()).thenReturn(application);
		NotFoundException exception = mock(NotFoundException.class);
		when(resolver.resolve(context)).thenThrow(exception);
		router.route(context);
		verify(handler).handle(request, response, exception);
	}
}