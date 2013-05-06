/**
 * 
 */
package org.minnal.core.server.exception;

import java.util.HashMap;
import java.util.Map;

import org.minnal.core.Request;
import org.minnal.core.Response;

/**
 * @author ganeshs
 *
 */
public class ExceptionHandler {
	
	private Map<Class<? extends Exception>, Class<? extends ApplicationException>> exceptionMap = 
			new HashMap<Class<? extends Exception>, Class<? extends ApplicationException>>();

	public void handle(Request request, Response response, Throwable throwable) {
		ApplicationException e = null;
		if (throwable instanceof ApplicationException) {
			e = (ApplicationException) throwable;
		} else {
			e = getMappedException(throwable);
		}
		if (e == null) {
			e = new InternalServerErrorException(throwable.getMessage(), throwable);
		}
		response.setStatus(e.getStatus());
	}
	
	public void mapException(Class<? extends Exception> from, Class<? extends ApplicationException> to) {
		exceptionMap.put(from, to);
	}
	
	public ApplicationException getMappedException(Throwable throwable) {
		Class<?> clazz = exceptionMap.get(throwable.getClass());
		if (clazz == null) {
			return null;
		}
		try {
			return (ApplicationException) clazz.getConstructor(String.class, Throwable.class).newInstance();
		} catch (Exception e) {
			// TODO log and return null
			return null;
		}
	}
}
