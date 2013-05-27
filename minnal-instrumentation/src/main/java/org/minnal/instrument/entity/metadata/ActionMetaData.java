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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActionMetaData other = (ActionMetaData) obj;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		return true;
	}
}
