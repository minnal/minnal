/**
 * 
 */
package org.minnal.core.route;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.server.exception.ApplicationException;
import org.minnal.core.server.exception.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class Action {

	private Object resource;
	
	private Method method;
	
	private static final Logger logger = LoggerFactory.getLogger(Action.class);
	
	public Action(Object resource, Method method) {
		this.resource = resource;
		this.method = method;
	}
	
	public Object invoke(Request request, Response response) {
		try {
			return method.invoke(resource, request, response);
		} catch (InvocationTargetException e) {
			Throwable throwable = e.getCause();
			if (throwable instanceof ApplicationException) {
				throw (ApplicationException) throwable;
			} else {
				throw new InternalServerErrorException(throwable);
			}
		} catch (ApplicationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Error while invoking the method - " + method, e);
			throw new InternalServerErrorException(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result
				+ ((resource == null) ? 0 : resource.getClass().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Action other = (Action) obj;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.getClass().equals(other.resource.getClass()))
			return false;
		return true;
	}
}
