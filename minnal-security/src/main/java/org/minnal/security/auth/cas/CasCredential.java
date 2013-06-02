/**
 * 
 */
package org.minnal.security.auth.cas;

import org.minnal.security.auth.Credential;

/**
 * @author ganeshs
 *
 */
public class CasCredential implements Credential {

	private String ticket;
	
	private String serviceUrl;
	
	/**
	 * @param ticket
	 * @param serviceUrl
	 */
	public CasCredential(String ticket, String serviceUrl) {
		this.ticket = ticket;
		this.serviceUrl = serviceUrl;
	}

	/**
	 * @return the ticket
	 */
	public String getTicket() {
		return ticket;
	}

	/**
	 * @return the serviceUrl
	 */
	public String getServiceUrl() {
		return serviceUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((serviceUrl == null) ? 0 : serviceUrl.hashCode());
		result = prime * result + ((ticket == null) ? 0 : ticket.hashCode());
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
		CasCredential other = (CasCredential) obj;
		if (serviceUrl == null) {
			if (other.serviceUrl != null)
				return false;
		} else if (!serviceUrl.equals(other.serviceUrl))
			return false;
		if (ticket == null) {
			if (other.ticket != null)
				return false;
		} else if (!ticket.equals(other.ticket))
			return false;
		return true;
	}
}
