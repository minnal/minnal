/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ganeshs
 *
 */
public class ActionMetaData extends MetaData {
	
	private List<ParameterMetaData> parameters = new ArrayList<ParameterMetaData>();
	
	private Method method;
	
	private String path;
	
	/**
	 * @param name
	 * @param path
	 * @param method
	 */
	public ActionMetaData(String name, String path, Method method) {
		super(name);
		this.method = method;
		this.path = path;
	}

	public List<ParameterMetaData> getParameters() {
		return Collections.unmodifiableList(parameters);
	}
	
	public void addParameter(ParameterMetaData parameter) {
		parameters.add(parameter);
	}

	public Method getMethod() {
		return method;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
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
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		return true;
	}
}
