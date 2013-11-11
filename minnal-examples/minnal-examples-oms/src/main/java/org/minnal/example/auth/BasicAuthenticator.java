/**
 * 
 */
package org.minnal.example.auth;

import java.util.HashMap;
import java.util.Map;

import org.minnal.security.auth.User;
import org.minnal.security.auth.basic.AbstractBasicAuthenticator;
import org.minnal.security.auth.basic.BasicCredential;

import com.google.common.collect.Maps;

/**
 * @author ganeshs
 *
 */
public class BasicAuthenticator extends AbstractBasicAuthenticator {
	
	private static Map<BasicCredential, User> validUsers = new HashMap<BasicCredential, User>();
	
	static {
		for (int i=0; i < 10; i++) {
			String user = "user" + i; 
			validUsers.put(new BasicCredential(user, "password"), new User(user, Maps.<String, Object>newHashMap()));
		}
	}
	
	@Override
	public User authenticate(BasicCredential credential) {
		return validUsers.get(credential);
	}

}
