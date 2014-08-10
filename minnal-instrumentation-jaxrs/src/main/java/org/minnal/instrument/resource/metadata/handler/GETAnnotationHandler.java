/**
 * 
 */
package org.minnal.instrument.resource.metadata.handler;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;

/**
 * @author ganeshs
 *
 */
public class GETAnnotationHandler extends HttpMethodAnnotationHandler {
	
	public Class<?> getAnnotationType() {
		return GET.class;
	}
	
	@Override
	protected String getHttpMethod() {
		return HttpMethod.GET;
	}
}
