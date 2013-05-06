/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ganeshs
 *
 */
public class ActionMetaData {
	
	private String name;
	
	private Set<ParameterMetaData> parameters = new HashSet<ParameterMetaData>();
	
	private Method method;
	
	public ActionMetaData(String name, Method method) {
		this.name = name;
		this.method = method;
	}

	public String getName() {
		return name;
	}

	public Set<ParameterMetaData> getParameters() {
		return Collections.unmodifiableSet(parameters);
	}
	
	public void addParameter(ParameterMetaData parameter) {
		parameters.add(parameter);
	}

	public Method getMethod() {
		return method;
	}
}
