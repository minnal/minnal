/**
 * 
 */
package org.minnal.security.auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.glassfish.jersey.message.internal.OutboundMessageContext;
import org.minnal.core.MinnalException;
import org.minnal.security.session.Session;
import org.pac4j.core.context.WebContext;

/**
 * @author ganeshs
 *
 */
public class JaxrsWebContext implements WebContext {
	
	private ContainerRequestContext request;
	
	private Response response;
	
	private OutboundMessageContext context;
	
	private Session session;

	/**
	 * @param request
	 * @param response
	 * @param session
	 */
	public JaxrsWebContext(ContainerRequestContext request, OutboundMessageContext context, Session session) {
		this.request = request;
		this.context = context;
		this.session = session;
		this.response = new OutboundJaxrsResponse.Builder(context).build();
	}

	/**
	 * @return the request
	 */
	public ContainerRequestContext getRequest() {
		return request;
	}

	/**
	 * @return the response
	 */
	public Response getResponse() {
		return response;
	}

	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	@Override
	public String getRequestParameter(String name) {
		return request.getUriInfo().getQueryParameters().getFirst(name);
	}

	@Override
	public Map<String, String[]> getRequestParameters() {
		Set<Entry<String, List<String>>> entries = request.getUriInfo().getQueryParameters().entrySet();
		Map<String, String[]> params = new HashMap<String, String[]>();
		for (Entry<String, List<String>> entry : entries) {
			params.put(entry.getKey(), entry.getValue().toArray(new String[0]));
		}
		return params;
	}

	@Override
	public String getRequestHeader(String name) {
		return request.getHeaderString(name);
	}

	@Override
	public void setSessionAttribute(String name, Object value) {
		session.addAttribute(name, value);
	}

	@Override
	public Object getSessionAttribute(String name) {
		return session.getAttribute(name);
	}

	@Override
	public String getRequestMethod() {
		return request.getMethod();
	}

	@Override
	public void writeResponseContent(String content) {
		try {
			context.getEntityStream().write(content.getBytes());
		} catch (IOException e) {
			// TODO log error
			throw new MinnalException(e);
		}
	}

	@Override
	public void setResponseStatus(final int code) {
		StatusType type = Response.Status.fromStatusCode(code);
		if (type == null) {
			type = new StatusType() {
				@Override
				public int getStatusCode() {
					return code;
				}
				
				@Override
				public String getReasonPhrase() {
					return null;
				}
				
				@Override
				public Family getFamily() {
					return Family.familyOf(code);
				}
			};
		}
		response = new OutboundJaxrsResponse(type, context);
	}

	@Override
	public void setResponseHeader(String name, String value) {
		response.getHeaders().add(name, value);
	}

	public String getServerName() {
		return request.getUriInfo().getRequestUri().getHost();
	}

	@Override
	public int getServerPort() {
		return request.getUriInfo().getRequestUri().getPort();
	}

	@Override
	public String getScheme() {
		return request.getUriInfo().getRequestUri().getScheme();
	}

	@Override
	public String getFullRequestURL() {
		return request.getUriInfo().getRequestUri().toASCIIString();
	}
}
