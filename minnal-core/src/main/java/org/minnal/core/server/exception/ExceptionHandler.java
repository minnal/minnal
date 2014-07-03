/**
 * 
 */
package org.minnal.core.server.exception;

import org.minnal.core.Request;
import org.minnal.core.Response;


/**
 * @author ganeshs
 *
 */
public interface ExceptionHandler {
	
	public void handle(Request request, Response response, Throwable exception);
}
