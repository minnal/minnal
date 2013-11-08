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

	private CasConfiguration casConfiguration;
	
	private SessionStore sessionStore;
	
	private long sessionExpiryTimeInSecs;
	
	private List<String> whiteListedUrls = new ArrayList<String>();
	
	private Authorizer authorizer = new SimpleAuthorizer();
	
	private String realm = DEFAULT_REALM;
	
	private static final String DEFAULT_REALM = "Service Apis";
	
	public SecurityConfiguration() {
	}

	/**
	 * @param casConfiguration
	 * @param sessionStore
	 * @param sessionExpiryTimeInSecs
	 */
	public SecurityConfiguration(CasConfiguration casConfiguration,
			SessionStore sessionStore, long sessionExpiryTimeInSecs) {
		this.casConfiguration = casConfiguration;
		this.sessionStore = sessionStore;
		this.sessionExpiryTimeInSecs = sessionExpiryTimeInSecs;
	}

	/**
	 * @return the casConfiguration
	 */
	public CasConfiguration getCasConfiguration() {
		return casConfiguration;
	}

	/**
	 * @param casConfiguration the casConfiguration to set
	 */
	public void setCasConfiguration(CasConfiguration casConfiguration) {
		this.casConfiguration = casConfiguration;
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

	/**
	 * @return the realm
	 */
	public String getRealm() {
		return realm;
	}

	/**
	 * @param realm the realm to set
	 */
	public void setRealm(String realm) {
		this.realm = realm;
	}

	/**
	 * @return the defaultRealm
	 */
	public static String getDefaultRealm() {
		return DEFAULT_REALM;
	}
}
