/**
 * 
 */
package org.minnal.core.config;


/**
 * Abstract class for all container, applications, resources and routes. The configuration can optionally inherit properties from its parent.
 * 
 * @author ganeshs
 *
 */
public abstract class Configuration {
	
	private Configuration parent;
	
	private String name;
	
	public Configuration() {
	}
	
	public Configuration(String name) {
		this(name, null);
	}
	
	public Configuration(String name, Configuration parent) {
		this.name = name;
		this.parent = parent;
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
	 * @return the parent
	 */
	public Configuration getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Configuration parent) {
		this.parent = parent;
	}
}
