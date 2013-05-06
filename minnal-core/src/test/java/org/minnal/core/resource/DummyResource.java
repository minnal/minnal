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
public interface DummyResource {
	void methodWithoutParameters();
	void methodWithValidParameters(Request request, Response response);
	void methodWithAdditionalParameters(Request request, Response response, Object additionalParam);
}
