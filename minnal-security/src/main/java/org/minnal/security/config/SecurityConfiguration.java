/**
 * 
 */
package org.minnal.security.config;

import java.util.ArrayList;
import java.util.List;

import org.minnal.security.auth.Authorizer;
import org.minnal.security.auth.SimpleAuthorizer;
import org.minnal.security.session.SessionStore;

/**
 * @author ganeshs
 *
 */
public class SecurityConfiguration {

	private SessionStore sessionStore;
	
	private long sessionExpiryTimeInSecs;
	
	private List<String> whiteListedUrls = new ArrayList<String>();
	
	private Authorizer authorizer = new SimpleAuthorizer();
	
	public SecurityConfiguration() {
	}

	/**
	 * @param sessionStore
	 * @param sessionExpiryTimeInSecs
	 */
	public SecurityConfiguration(SessionStore sessionStore, long sessionExpiryTimeInSecs) {
		this.sessionStore = sessionStore;
		this.sessionExpiryTimeInSecs = sessionExpiryTimeInSecs;
	}

	/**
	 * @return the sessionStore
	 */
	public SessionStore getSessionStore() {
		return sessionStore;
	}

	/**
	 * @param sessionStore the sessionStore to set
	 */
	public void setSessionStore(SessionStore sessionStore) {
		this.sessionStore = sessionStore;
	}

	/**
	 * @return the sessionExpiryTimeInSecs
	 */
	public long getSessionExpiryTimeInSecs() {
		return sessionExpiryTimeInSecs;
	}

	/**
	 * @param sessionExpiryTimeInSecs the sessionExpiryTimeInSecs to set
	 */
	public void setSessionExpiryTimeInSecs(long sessionExpiryTimeInSecs) {
		this.sessionExpiryTimeInSecs = sessionExpiryTimeInSecs;
	}

	/**
	 * @return the whiteListedUrls
	 */
	public List<String> getWhiteListedUrls() {
		return whiteListedUrls;
	}

	/**
	 * @param whiteListedUrls the whiteListedUrls to set
	 */
	public void setWhiteListedUrls(List<String> whitelistedUrls) {
		this.whiteListedUrls = whitelistedUrls;
	}

	/**
	 * @return the authorizer
	 */
	public Authorizer getAuthorizer() {
		return authorizer;
	}

	/**
	 * @param authorizer the authorizer to set
	 */
	public void setAuthorizer(Authorizer authorizer) {
		this.authorizer = authorizer;
	}
}
