/**
 * 
 */
package org.minnal.core.server.exception;

import java.util.HashMap;
import java.util.Map;

import org.minnal.core.Request;
import org.minnal.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class ExceptionResolver {

	private Map<Class<? extends Exception>, Class<? extends ApplicationException>> exceptionMap = 
			new HashMap<Class<? extends Exception>, Class<? extends ApplicationException>>();
	
	private Map<Class<? extends Exception>, ExceptionHandler> exceptionHandlers = new HashMap<Class<? extends Exception>, ExceptionHandler>();
	
	private static final Logger logger = LoggerFactory.getLogger(ExceptionResolver.class);
	
	/**
	 * Resolve the given exception and populate the response
	 * 
	 * @param request
	 * @param response
	 * @param throwable
	 */
	public void resolve(Request request, Response response, Throwable throwable) {
		logger.trace("Handling the exception - ", throwable);
		
		ExceptionHandler handler = exceptionHandlers.get(throwable.getClass());
		if (handler != null) {
			handler.handle(request, response, throwable);
		} else {
			ApplicationException e = null;
			if (throwable instanceof ApplicationException) {
				e = (ApplicationException) throwable;
			} else {
				e = getMappedException(throwable);
			}
			if (e == null) {
				logger.error("Couldn't resolve the exception. throwing internal server error", throwable);
				e = new InternalServerErrorException(throwable.getMessage(), throwable);
			}
			e.handle(response);
		}
		
	}
	
	/**
	 * Maps the exception from one to another exception
	 * 
	 * @param from
	 * @param to
	 */
	public void mapException(Class<? extends Exception> from, Class<? extends ApplicationException> to) {
		exceptionMap.put(from, to);
	}
	
	/**
	 * Adds the exception handler for the given exception class 
	 * 
	 * @param exception
	 * @param handler
	 */
	public void addExceptionHandler(Class<? extends Exception> exception, ExceptionHandler handler) {
		exceptionHandlers.put(exception, handler);
	}
	
	/**
	 * Returns the mapped exception for the given exception
	 * 
	 * @param throwable
	 * @return
	 */
	public ApplicationException getMappedException(Throwable throwable) {
		Class<?> clazz = exceptionMap.get(throwable.getClass());
		if (clazz == null) {
			return null;
		}
		try {
			return (ApplicationException) clazz.getConstructor(String.class, Throwable.class).newInstance(throwable.getMessage(), throwable);
		} catch (Exception e) {
			logger.debug("Failed while contructing the exception", e);
			return null;
		}
	}

	/**
	 * @return the exceptionMap
	 */
	public Map<Class<? extends Exception>, Class<? extends ApplicationException>> getExceptionMap() {
		return exceptionMap;
	}

	/**
	 * @return the exceptionHandlers
	 */
	public Map<Class<? extends Exception>, ExceptionHandler> getExceptionHandlers() {
		return exceptionHandlers;
	}

}
