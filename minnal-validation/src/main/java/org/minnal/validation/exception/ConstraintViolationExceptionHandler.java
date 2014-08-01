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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.ExceptionMapper;

import org.javalite.common.Inflector;
import org.minnal.validation.FieldError;

/**
 * @author ganeshs
 * 
 */
public class ConstraintViolationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(ConstraintViolationException exception) {
		ConstraintViolationException ex = (ConstraintViolationException) exception;
		List<FieldError> errors = new ArrayList<FieldError>();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errors.add(new FieldError(Inflector.underscore(violation.getPropertyPath().toString()), violation.getMessage(), violation.getInvalidValue()));
		}
		Map<String, List<FieldError>> message = new HashMap<String, List<FieldError>>();
		message.put("fieldErrors", errors);
		return Response.status(UnprocessableEntityStatusType.INSTANCE).entity(message).build();
	}

	private static class UnprocessableEntityStatusType implements StatusType {
		
		private static UnprocessableEntityStatusType INSTANCE = new UnprocessableEntityStatusType();
		
		/**
		 * Singleton constructor
		 */
		private UnprocessableEntityStatusType() {
		}
		
		@Override
		public int getStatusCode() {
			return 422;
		}

		@Override
		public String getReasonPhrase() {
			return "Unprocessable Entity";
		}

		@Override
		public Family getFamily() {
			return Family.CLIENT_ERROR;
		}
	}

}
