/**
 * 
 */
package org.minnal.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.server.exception.ApplicationException;
import org.minnal.core.server.exception.BadRequestException;
import org.minnal.core.server.exception.ExceptionHandler;
import org.minnal.core.server.exception.ExceptionResolver;
import org.minnal.core.server.exception.InternalServerErrorException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ExceptionResolverTest {
	
	private ExceptionResolver resolver;
	
	@BeforeMethod
	public void setup() {
		resolver = new ExceptionResolver();
	}

	@Test
	public void shouldMapExceptions() {
		resolver.mapException(NullPointerException.class, InternalServerErrorException.class);
		assertEquals(resolver.getExceptionMap().get(NullPointerException.class), InternalServerErrorException.class);
	}
	
	@Test
	public void shouldAddExceptionHandler() {
		ExceptionHandler handler = new ExceptionHandler() {
			@Override
			public void handle(Request request, Response response, Throwable exception) {
			}
		};
		resolver.addExceptionHandler(NullPointerException.class, handler);
		assertEquals(resolver.getExceptionHandlers().get(NullPointerException.class), handler);
	}
	
	@Test
	public void shouldGetMappedException() {
		resolver.mapException(NullPointerException.class, InternalServerErrorException.class);
		Exception npe = new NullPointerException("test123");
		ApplicationException exception = resolver.getMappedException(npe);
		assertTrue(exception instanceof InternalServerErrorException);
		assertEquals(exception.getMessage(), "test123");
		assertEquals(exception.getCause(), npe);
	}
	
	@Test
	public void shouldNotGetMappedExceptionIfMappinNotFound() {
		resolver.mapException(NullPointerException.class, InternalServerErrorException.class);
		Exception npe = new IllegalStateException("test123");
		assertNull(resolver.getMappedException(npe));
	}
	
	@Test
	public void shouldNotMappedExceptionIfExceptionCantBeConstructed() {
		resolver.mapException(NullPointerException.class, ApplicationException.class);
		Exception npe = new NullPointerException("test123");
		assertNull(resolver.getMappedException(npe));
	}
	
	@Test
	public void shouldResolveNonApplicationExceptionToInternalServerError() {
		Response response = mock(Response.class);
		resolver.resolve(mock(Request.class), response, new NullPointerException());
		verify(response).setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Test
	public void shouldResolveApplicationExeception() {
		Response response = mock(Response.class);
		BadRequestException exception = mock(BadRequestException.class);
		resolver.resolve(mock(Request.class), response, exception);
		verify(exception).handle(response);
	}
	
	@Test
	public void shouldResolveMappedExeception() {
		Response response = mock(Response.class);
		resolver.mapException(NullPointerException.class, BadRequestException.class);
		resolver.resolve(mock(Request.class), response, new NullPointerException());
		verify(response).setStatus(HttpResponseStatus.BAD_REQUEST);
	}
	
	@Test
	public void shouldResolveToInternalServerErrorIfMappedExeceptionNotFound() {
		Response response = mock(Response.class);
		resolver.resolve(mock(Request.class), response, new NullPointerException());
		verify(response).setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Test
	public void shouldResolveExceptionHandler() {
		Response response = mock(Response.class);
		Request request = mock(Request.class);
		ExceptionHandler handler = mock(ExceptionHandler.class);
		NullPointerException npe = new NullPointerException();
		resolver.addExceptionHandler(NullPointerException.class, handler);
		resolver.resolve(request, response, npe);
		verify(handler).handle(request, response, npe);
	}
	
	@Test
	public void shouldGivePriorityToExceptionHandlerThanMapping() {
		Response response = mock(Response.class);
		Request request = mock(Request.class);
		ExceptionHandler handler = mock(ExceptionHandler.class);
		NullPointerException npe = new NullPointerException();
		resolver.addExceptionHandler(NullPointerException.class, handler);
		resolver.mapException(NullPointerException.class, InternalServerErrorException.class);
		resolver.resolve(request, response, npe);
		verify(handler).handle(request, response, npe);
	}
}
