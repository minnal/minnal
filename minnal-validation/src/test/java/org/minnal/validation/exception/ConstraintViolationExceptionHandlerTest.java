/**
 * 
 */
package org.minnal.validation.exception;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.validation.FieldError;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 * @author ganeshs
 * 
 */
public class ConstraintViolationExceptionHandlerTest {

	@Test
	public void shouldHandleException() {
		ConstraintViolation<?> violation = mock(ConstraintViolation.class);
		Path path = mock(Path.class);
		when(path.toString()).thenReturn("dummyField");
		when(violation.getPropertyPath()).thenReturn(path);
		when(violation.getMessage()).thenReturn("dummy message");
		when(violation.getInvalidValue()).thenReturn("dummy");
		ConstraintViolationException exception = new ConstraintViolationException(Sets.newHashSet(violation));
		ConstraintViolationExceptionHandler handler = new ConstraintViolationExceptionHandler();
		Response response = mock(Response.class);
		handler.handle(mock(Request.class), response, exception);
		Map<String, List<FieldError>> message = new HashMap<String, List<FieldError>>();
		message.put("field_errors", Arrays.asList(new FieldError("dummy_field", "dummy message", "dummy")));
		verify(response).setContent(message);
		verify(response).setStatus(HttpResponseStatus.UNPROCESSABLE_ENTITY);
	}
}
