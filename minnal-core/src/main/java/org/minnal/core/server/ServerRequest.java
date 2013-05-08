/**
 * 
 */
package org.minnal.core.server;

import java.net.SocketAddress;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.minnal.core.Request;
import org.minnal.core.util.HttpUtil;

/**
 * @author ganeshs
 *
 */
public class ServerRequest extends ServerMessage implements Request {

	private String applicationPath;
	
	private HttpRequest request;
	
	private SocketAddress remoteAddress;
	
	public ServerRequest(HttpRequest request, SocketAddress remoteAddress) {
		super(request);
		this.remoteAddress = remoteAddress;
		this.request = request;
		parseQueryParams();
	}
	
	private void parseQueryParams() {
		addHeaders(HttpUtil.getQueryParameters(getPath()));
	}

	/**
	 * @return the applicationPath
	 */
	public String getApplicationPath() {
		return applicationPath;
	}
	
	/**
	 * @param applicationPath the applicationPath to set
	 */
	public void setApplicationPath(String applicationPath) {
		this.applicationPath = applicationPath;
	}
	
	public String getPath() {
		return request.getUri();
	}
	
	public String getRelativePath() {
		if (getApplicationPath() != null) {
			return getPath().substring(getApplicationPath().length());
		}
		return getPath();
	}
	
	public HttpMethod getHttpMethod() {
		return request.getMethod();
	}

	/**
	 * @return the remoteAddress
	 */
	public SocketAddress getRemoteAddress() {
		return remoteAddress;
	}

}
