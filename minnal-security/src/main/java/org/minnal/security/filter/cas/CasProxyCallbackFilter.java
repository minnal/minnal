/**
 * 
 */
package org.minnal.security.filter.cas;

import java.net.URI;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.Filter;
import org.minnal.core.FilterChain;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.util.HttpUtil;
import org.minnal.security.config.SecurityConfiguration;

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

}
