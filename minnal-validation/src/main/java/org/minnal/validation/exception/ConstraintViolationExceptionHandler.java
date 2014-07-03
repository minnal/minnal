/**
 * 
 */
package org.minnal.validation.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.javalite.common.Inflector;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.server.exception.ExceptionHandler;
import org.minnal.validation.FieldError;

/**
 * @author ganeshs
 *
 */
public class ConstraintViolationExceptionHandler implements ExceptionHandler {
	
	@Override
	public void handle(Request request, Response response, Throwable exception) {
		ConstraintViolationException ex = (ConstraintViolationException) exception;
		List<FieldError> errors = new ArrayList<FieldError>();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errors.add(new FieldError(Inflector.underscore(violation.getPropertyPath().toString()), violation.getMessage(), violation.getInvalidValue()));
		}
		Map<String, List<FieldError>> message = new HashMap<String, List<FieldError>>();
		message.put("field_errors", errors);
		response.setStatus(HttpResponseStatus.UNPROCESSABLE_ENTITY);
		response.setContent(message);
	}

}
