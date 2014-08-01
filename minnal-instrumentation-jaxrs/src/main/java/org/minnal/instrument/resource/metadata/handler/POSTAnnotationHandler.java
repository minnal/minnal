/**
 * 
 */
package org.minnal.instrument.resource.metadata.handler;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;

/**
 * @author ganeshs
 *
 */
public class POSTAnnotationHandler extends HttpMethodAnnotationHandler {
	
	public Class<?> getAnnotationType() {
		return POST.class;
	}
	
	@Override
	protected String getHttpMethod() {
		return HttpMethod.POST;
	}
}
