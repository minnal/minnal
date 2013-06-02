/**
 * 
 */
package org.minnal.security.auth;

/**
 * @author ganeshs
 *
 */
public class SimpleRole implements Role {
	
	private String name;
	
	/**
	 * @param name
	 */
	public SimpleRole(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
