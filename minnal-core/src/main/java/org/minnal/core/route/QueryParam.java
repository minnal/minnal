/**
 * 
 */
package org.minnal.core.route;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author ganeshs
 *
 */
public class QueryParam {
	
	public enum Type {
		string, integer, decimal, bool, character;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public static Type typeOf(Class clazz) {
			if (clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(BigInteger.class)) {
				return integer;
			}
			if (clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(BigDecimal.class)) {
				return decimal;
			}
			if (clazz.isAssignableFrom(Boolean.class)) {
				return bool;
			}
			if (clazz.isAssignableFrom(Character.class)) {
				return character;
			}
			return string;
		}
	}

	private String name;
	
	private Type type;
	
	private String description;
	

	/**
	 * @param name
	 * @param type
	 */
	public QueryParam(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	/**
	 * @param name
	 * @param type
	 * @param description
	 */
	public QueryParam(String name, Type type, String description) {
		this.name = name;
		this.type = type;
		this.description = description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		QueryParam other = (QueryParam) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "QueryParam [name=" + name + "]";
	}
}
