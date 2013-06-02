/**
 * 
 */
package org.minnal.security.config;

import javax.net.ssl.HostnameVerifier;

import org.jasig.cas.client.ssl.AnyHostnameVerifier;
import org.minnal.security.auth.cas.AbstractPgtTicketStorage;
import org.minnal.security.auth.cas.JpaPgtStorage;

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
	public void setTicketStorage(JpaPgtStorage ticketStorage) {
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
}
