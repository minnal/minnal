/**
 * 
 */
package org.minnal.instrument.resource.metadata;

import java.lang.reflect.Method;

import org.minnal.utils.route.RoutePattern;

/**
 * @author ganeshs
 *
 */
public class ResourceMethodMetaData {

	private String path;
	
	private String httpMethod;
	
	private RoutePattern pattern;
	
	private Method method;

	/**
	 * @param path
	 * @param httpMethod
	 * @param method
	 */
	public ResourceMethodMetaData(String path, String httpMethod, Method method) {
		this.path = path;
		this.httpMethod = httpMethod;
		this.pattern = new RoutePattern(path);
		this.method = method;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @return the httpMethod
	 */
	public String getHttpMethod() {
		return httpMethod;
	}

	/**
	 * @param httpMethod the httpMethod to set
	 */
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	/**
	 * @return the pattern
	 */
	public RoutePattern getPattern() {
		return pattern;
	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((httpMethod == null) ? 0 : httpMethod.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		ResourceMethodMetaData other = (ResourceMethodMetaData) obj;
		if (httpMethod == null) {
			if (other.httpMethod != null)
				return false;
		} else if (!httpMethod.equals(other.httpMethod))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
}
