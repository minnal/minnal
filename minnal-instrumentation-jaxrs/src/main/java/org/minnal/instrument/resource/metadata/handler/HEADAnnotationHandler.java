/**
 * 
 */
package org.minnal.instrument.resource.metadata.handler;

import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;

/**
 * @author ganeshs
 *
 */
public class HEADAnnotationHandler extends HttpMethodAnnotationHandler {
	
	public Class<?> getAnnotationType() {
		return HEAD.class;
	}
	
	@Override
	protected String getHttpMethod() {
		return HttpMethod.HEAD;
	}
}
