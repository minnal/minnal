/**
 * 
 */
package org.minnal.instrument.resource.metadata.handler;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;

/**
 * @author ganeshs
 *
 */
public class OPTIONSAnnotationHandler extends HttpMethodAnnotationHandler {
	
	public Class<?> getAnnotationType() {
		return OPTIONS.class;
	}
	
	@Override
	protected String getHttpMethod() {
		return HttpMethod.OPTIONS;
	}
}
