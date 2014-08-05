/**
 * 
 */
package org.minnal.security.filter.cas;

import io.netty.handler.codec.http.HttpResponseStatus;

import java.net.URI;

import javax.servlet.FilterChain;

import org.minnal.security.config.SecurityConfiguration;
import org.minnal.utils.http.HttpUtil;

/**
 * @author ganeshs
 *
 */
public class CasProxyCallbackFilter implements Filter {
	
	private SecurityConfiguration configuration;
	
	/**
     * Constant representing the ProxyGrantingTicket IOU Request Parameter.
     */
    public static final String PARAM_PROXY_GRANTING_TICKET_IOU = "pgtIou";

    /**
     * Constant representing the ProxyGrantingTicket Request Parameter.
     */
    public static final String PARAM_PROXY_GRANTING_TICKET = "pgtId";
	
	/**
	 * @param configuration
	 */
	public CasProxyCallbackFilter(SecurityConfiguration configuration) {
		this.configuration = configuration;
	}

	public void doFilter(Request request, Response response, FilterChain chain) {
		URI uri = HttpUtil.createURI(configuration.getCasConfiguration().getCasProxyCallbackUrl());
		if (! request.getRelativePath().endsWith(uri.getPath())) {
			chain.doFilter(request, response);
			return;
		}
		configuration.getCasConfiguration().getTicketStorage().save(request.getHeader(PARAM_PROXY_GRANTING_TICKET_IOU), 
				request.getHeader(PARAM_PROXY_GRANTING_TICKET));
		response.setStatus(HttpResponseStatus.OK);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((configuration == null) ? 0 : configuration.hashCode());
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
		CasProxyCallbackFilter other = (CasProxyCallbackFilter) obj;
		if (configuration == null) {
			if (other.configuration != null)
				return false;
		} else if (!configuration.equals(other.configuration))
			return false;
		return true;
	}

}
