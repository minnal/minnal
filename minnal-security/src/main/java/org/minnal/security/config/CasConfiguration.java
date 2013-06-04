/**
 * 
 */
package org.minnal.security.config;

import javax.net.ssl.HostnameVerifier;

import org.jasig.cas.client.ssl.AnyHostnameVerifier;
import org.minnal.security.auth.cas.AbstractPgtTicketStorage;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ganeshs
 *
 */
public class CasConfiguration {

	@JsonProperty(required=true)
	private String casServerUrl;
	
	@JsonProperty(required=true)
	private String casProxyCallbackUrl;
	
	@JsonProperty(required=true)
	private AbstractPgtTicketStorage ticketStorage;
	
	private boolean enableSingleSignout = true;
	
	private String logoutParameterName = "logoutRequest";
	
	private HostnameVerifier hostnameVerifier = new AnyHostnameVerifier();
	
	public CasConfiguration() {
	}

	/**
	 * @param casServerUrl
	 * @param casProxyCallbackUrl
	 * @param ticketStorage
	 * @param hostnameVerifier
	 */
	public CasConfiguration(String casServerUrl, String casProxyCallbackUrl,
			AbstractPgtTicketStorage ticketStorage,
			HostnameVerifier hostnameVerifier) {
		this.casServerUrl = casServerUrl;
		this.casProxyCallbackUrl = casProxyCallbackUrl;
		this.ticketStorage = ticketStorage;
		this.hostnameVerifier = hostnameVerifier;
	}

	/**
	 * @return the casServerUrl
	 */
	public String getCasServerUrl() {
		return casServerUrl;
	}

	/**
	 * @param casServerUrl the casServerUrl to set
	 */
	public void setCasServerUrl(String casServerUrl) {
		this.casServerUrl = casServerUrl;
	}

	/**
	 * @return the casProxyCallbackUrl
	 */
	public String getCasProxyCallbackUrl() {
		return casProxyCallbackUrl;
	}

	/**
	 * @param casProxyCallbackUrl the casProxyCallbackUrl to set
	 */
	public void setCasProxyCallbackUrl(String casProxyCallbackUrl) {
		this.casProxyCallbackUrl = casProxyCallbackUrl;
	}

	/**
	 * @return the ticketStorage
	 */
	public AbstractPgtTicketStorage getTicketStorage() {
		return ticketStorage;
	}

	/**
	 * @param ticketStorage the ticketStorage to set
	 */
	public void setTicketStorage(AbstractPgtTicketStorage ticketStorage) {
		this.ticketStorage = ticketStorage;
	}

	/**
	 * @return the hostnameVerifier
	 */
	public HostnameVerifier getHostnameVerifier() {
		return hostnameVerifier;
	}

	/**
	 * @param hostnameVerifier the hostnameVerifier to set
	 */
	public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
		this.hostnameVerifier = hostnameVerifier;
	}

	/**
	 * @return the enableSingleSignout
	 */
	public boolean isEnableSingleSignout() {
		return enableSingleSignout;
	}

	/**
	 * @param enableSingleSignout the enableSingleSignout to set
	 */
	public void setEnableSingleSignout(boolean enableSingleSignout) {
		this.enableSingleSignout = enableSingleSignout;
	}

	/**
	 * @return the logoutParameterName
	 */
	public String getLogoutParameterName() {
		return logoutParameterName;
	}

	/**
	 * @param logoutParameterName the logoutParameterName to set
	 */
	public void setLogoutParameterName(String logoutParameterName) {
		this.logoutParameterName = logoutParameterName;
	}

}
