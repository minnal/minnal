/**
 * 
 */
package org.minnal.core.server;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.Response;
import org.minnal.core.route.Route;

import com.google.common.collect.FluentIterable;
import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
public class ServerResponse extends ServerMessage implements Response {
	
	private HttpResponse response;
	
	private ServerRequest request;

	public ServerResponse(ServerRequest request, HttpResponse response) {
		super(response);
		this.request = request;
		this.response = response;
	}

	public void setStatus(HttpResponseStatus status) {
		response.setStatus(status);
	}
	
	public void setContent(Object content) {
		MediaType type = FluentIterable.from(request.getSupportedAccepts()).first().or(getResolvedRoute().getConfiguration().getDefaultMediaType());
		setContentType(type);
		setContent(getSerializer(type).serialize(content));
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
}
