/**
 * 
 */
package org.minnal.security.auth;

/**
 * @author ganeshs
 *
 */
public interface Authenticator<C extends Credential, P extends Principal> {

	P authenticate(C credential);
}
