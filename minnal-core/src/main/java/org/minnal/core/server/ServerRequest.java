/**
 * 
 */
package org.minnal.core.server;

import java.net.SocketAddress;
import java.net.URI;
import java.util.Set;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.minnal.core.MinnalException;
import org.minnal.core.Request;
import org.minnal.core.route.Route;
import org.minnal.core.serializer.Serializer;
import org.minnal.core.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.net.MediaType;

/**
 * The incoming http request. This wraps the netty http request underneath and exposes additional utility method to deserialize the content.
 * 
 * @author ganeshs
 *
 */
public class ServerRequest extends ServerMessage implements Request {

	private String applicationPath;
	
	private HttpRequest request;
	
	private SocketAddress remoteAddress;
	
	private URI uri;
	
	private Set<MediaType> accepts;
	
	private Set<MediaType> supportedAccepts;
	
	private MediaType contentType;
	
	private static final Logger logger = LoggerFactory.getLogger(ServerRequest.class);
	
	public ServerRequest(HttpRequest request, SocketAddress remoteAddress) {
		super(request);
		this.remoteAddress = remoteAddress;
		init(request);
	}
	
	private void init(HttpRequest request) {
		logger.trace("Initializing the request {}", request);
		this.request = request;
		uri = HttpUtil.createURI(request.getUri());
		if (containsHeader(HttpHeaders.Names.CONTENT_TYPE)) {
			contentType = MediaType.parse(getHeader(HttpHeaders.Names.CONTENT_TYPE));
		}
		if (containsHeader(HttpHeaders.Names.ACCEPT)) {
			accepts = FluentIterable.from(Splitter.on(",").split(getHeader(HttpHeaders.Names.ACCEPT))).transform(new Function<String, MediaType>() {
				public MediaType apply(String input) {
					MediaType type = MediaType.parse(input.trim());
					if (! type.parameters().containsKey("UTF-8")) {
						return type.withoutParameters().withCharset(DEFAULT_CHARSET);
					}
					return type;
				}
			}).toSet();
		}
		addHeaders(HttpUtil.getQueryParameters(uri));
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
	
	public URI getUri() {
		return uri;
	}
	
	public String getRelativePath() {
		if (getApplicationPath() != null) {
			return getUri().getPath().substring(getApplicationPath().length());
		}
		return getUri().getPath();
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
	
	public MediaType getContentType() {
		return contentType;
	}

	public Set<MediaType> getAccepts() {
		return accepts;
	}

	public <T> T getContentAs(Class<T> clazz) {
		Serializer serializer = getSerializer(getContentType());
		if (serializer == null) {
			throw new MinnalException("Serializer not found for the content type - " + getContentType());
		}
		return serializer.deserialize(getContent(), clazz);
	}
	
	@Override
	public void setResolvedRoute(Route resolvedRoute) {
		super.setResolvedRoute(resolvedRoute);
	}

	/**
	 * @return the supportedAccepts
	 */
	public Set<MediaType> getSupportedAccepts() {
		return supportedAccepts;
	}

	/**
	 * @param supportedAccepts the supportedAccepts to set
	 */
	public void setSupportedAccepts(Set<MediaType> supportedAccepts) {
		this.supportedAccepts = supportedAccepts;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServerRequest [applicationPath=")
				.append(applicationPath).append(", request=").append(request)
				.append(", remoteAddress=").append(remoteAddress)
				.append(", uri=").append(uri).append(", accepts=")
				.append(accepts).append(", supportedAccepts=")
				.append(supportedAccepts).append(", contentType=")
				.append(contentType).append("]");
		return builder.toString();
	}
}
