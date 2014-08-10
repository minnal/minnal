/**
 * 
 */
package org.minnal.api.filter;

import java.util.List;
import java.util.Map;

import com.wordnik.swagger.jaxrs.filter.JaxrsFilter;
import com.wordnik.swagger.model.ApiDescription;
import com.wordnik.swagger.model.Operation;
import com.wordnik.swagger.model.Parameter;

/**
 * @author ganeshs
 *
 */
public class MinnalApiSpecFilter extends JaxrsFilter {

	@Override
	public boolean isParamAllowed(Parameter parameter, Operation operation, ApiDescription api, Map<String, List<String>> params,
			Map<String, String> cookies, Map<String, List<String>> headers) {
		if (parameter.paramAccess().nonEmpty()) {
			return !parameter.paramAccess().get().equals("internal");
		}
		return super.isParamAllowed(parameter, operation, api, params, cookies, headers);
	}

	
}
