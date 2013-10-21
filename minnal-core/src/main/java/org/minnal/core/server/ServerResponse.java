/**
 * 
 */
package org.minnal.core.server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.Response;
import org.minnal.core.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.FluentIterable;
import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
public class ServerResponse extends ServerMessage implements Response {
	
	private HttpResponse response;
	
	private ServerRequest request;
	
	private boolean contentSet;
	
	private ResponseWriter writer;
	
	private static Logger logger = LoggerFactory.getLogger(ServerResponse.class);

	public ServerResponse(ServerRequest request, HttpResponse response) {
		super(response);
		this.request = request;
		this.response = response;
		this.writer =  new DefaultResponseWriter(this);
		init();
	}
	
	protected void init() {
		// TODO: Allowing CORS for all domains. Take the value from configuration
		addHeader(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		addHeader(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_METHODS, "*");
		addHeader(HttpHeaders.Names.ACCESS_CONTROL_ALLOW_HEADERS, "*");
	}
	
	public MediaType getPrefferedContentType(){
		if (request.getSupportedAccepts() != null) {
			return FluentIterable.from(request.getSupportedAccepts()).first().or(this.getResolvedRoute().getConfiguration().getDefaultMediaType());
		} else {
			return MediaType.PLAIN_TEXT_UTF_8;
		}
	}
	
	/**
	 * @param writer
	 */
	public void setResponseWriter(ResponseWriter writer) {
		this.writer = writer;
	}

	public void setStatus(HttpResponseStatus status) {
		response.setStatus(status);
	}
	
	public void setContent(Object content) {
		if (! contentSet) {
			writer.write(content);
		}
	}
	
	@Override
	public void setContent(ChannelBuffer content) {
		super.setContent(content);
		contentSet = true;
	}
	
	public ChannelFuture write(Channel channel) {
		return channel.write(response);
	}
	
	public HttpResponseStatus getStatus() {
		return response.getStatus();
	}
	
	public void setContentType(MediaType type) {
		response.addHeader(HttpHeaders.Names.CONTENT_TYPE, type.toString());
	}
	
	public boolean isContentSet() {
		return contentSet;
	}
	
	@Override
	protected String getCookieHeaderName() {
		return HttpHeaders.Names.SET_COOKIE;
	}
	
	/**
	 * Returns the request associated with this response
	 * @return
	 */
	protected ServerRequest getRequest() {
		return request;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ServerResponse [response=").append(response)
				.append(", request=").append(request).append(", contentSet=")
				.append(contentSet).append("]");
		return builder.toString();
	}
}
