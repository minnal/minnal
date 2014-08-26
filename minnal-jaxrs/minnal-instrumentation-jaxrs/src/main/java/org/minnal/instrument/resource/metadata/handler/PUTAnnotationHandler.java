/**
 * 
 */
package org.minnal.instrument.resource.metadata.handler;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.PUT;

/**
 * @author ganeshs
 *
 */
public class PUTAnnotationHandler extends HttpMethodAnnotationHandler {
	
	public Class<?> getAnnotationType() {
		return PUT.class;
	}
	
	@Override
	protected String getHttpMethod() {
		return HttpMethod.PUT;
	}
}
