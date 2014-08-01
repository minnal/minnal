/**
 * 
 */
package org.minnal.instrument.resource.metadata.handler;

import javax.ws.rs.DELETE;
import javax.ws.rs.HttpMethod;

/**
 * @author ganeshs
 *
 */
public class DELETEAnnotationHandler extends HttpMethodAnnotationHandler {
	
	public Class<?> getAnnotationType() {
		return DELETE.class;
	}
	
	@Override
	protected String getHttpMethod() {
		return HttpMethod.DELETE;
	}
}
