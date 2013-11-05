/**
 * 
 */
package org.minnal.security.auth;


/**
 * @author ganeshs
 *
 */
public interface Authenticator<C extends Credential, P extends Principal> {
	
	public static final String PRINCIPAL = "principal";
	
	public static final String SESSION = "session";

	P authenticate(C credential);
	
}
