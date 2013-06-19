/**
 * 
 */
package org.minnal.core.resource;

import org.minnal.core.Request;
import org.minnal.core.Response;

/**
 * @author ganeshs
 *
 */
public class DummyResource {
	public void methodWithoutParameters() {}
	public void methodWithValidParameters(Request request, Response response) {}
	public void methodWithAdditionalParameters(Request request, Response response, Object additionalParam) {}
}
