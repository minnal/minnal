/**
 * 
 */
package org.minnal.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.internal.MapPropertiesDelegate;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ResourceConfig;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.server.MessageContext;
import org.minnal.utils.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.Futures;


/**
 * @author ganeshs
 *
 */
public class Router {
	
	private ApplicationMapping applicationMapping;
	
	private RouterListener listener;
	
	private Map<Application<? extends ApplicationConfiguration>, ApplicationHandler> handlers = new HashMap<Application<? extends ApplicationConfiguration>, ApplicationHandler>();
	
	private static final Logger logger = LoggerFactory.getLogger(Router.class);
	
	/**
	 * @param applicationMapping
	 */
	public Router(ApplicationMapping applicationMapping) {
		this.applicationMapping = applicationMapping;
	}
	
	/**
	 * @param context
	 */
	public void route(MessageContext context) {
		logger.trace("Routing the context {}", context);
		
		Application<ApplicationConfiguration> application = applicationMapping.resolve(context.getRequest());
		if (application == null) {
			throw new NotFoundException("Request path not found");
		}
		context.setApplication(application);
		if (listener != null) {
			listener.onApplicationResolved(context);
		}
		
		ApplicationHandler handler = getApplicationHandler(application);
		ContainerRequest containerRequest = createContainerRequest(context);
		ByteBuf buffer = Unpooled.buffer();
		ContainerResponse response = null;
		try {
			response = Futures.getUnchecked(handler.apply(containerRequest, new ByteBufOutputStream(buffer)));
		} catch (Exception e) {
			logger.debug("Failed while handling the request - " + containerRequest, e);
			response = new ContainerResponse(containerRequest, Response.serverError().build());
		}
		UriInfo uriInfo = containerRequest.getUriInfo();
		List<String> matchedUris = uriInfo.getMatchedURIs();
		if (matchedUris != null && ! matchedUris.isEmpty()) {
			context.setMatchedRoute(matchedUris.get(0));
		}
		FullHttpResponse httpResponse = createHttpResponse(context, response, buffer);
		context.setResponse(httpResponse);
	}
	
	/**
	 * Returns the handler for the given application
	 * 
	 * @param application
	 * @return
	 */
	protected ApplicationHandler getApplicationHandler(Application<ApplicationConfiguration> application) {
		ApplicationHandler handler = handlers.get(application);
		if (handler == null) {
			handler = createApplicationHandler(application.getResourceConfig());
			handlers.put(application, handler);
		}
		return handler;
	}
	
	/**
	 * @param resourceConfig
	 * @return
	 */
	protected ApplicationHandler createApplicationHandler(ResourceConfig resourceConfig) {
		return new ApplicationHandler(resourceConfig);
	}
	
	/**
	 * Creates the container request from the http request
	 *  
	 * @param httpRequest
	 * @return
	 */
	protected ContainerRequest createContainerRequest(MessageContext context) {
		Application<ApplicationConfiguration> application = context.getApplication();
		FullHttpRequest httpRequest = context.getRequest();
		URI baseUri = URI.create(context.getBaseUri().resolve(application.getPath()) + "/");
		URI requestUri = HttpUtil.createURI(httpRequest.getUri());
		ContainerRequest containerRequest = new ContainerRequest(baseUri, requestUri, httpRequest.getMethod().name(), null, new MapPropertiesDelegate());
//		containerRequest.setProperty(REQUEST_PROPERTY_REMOTE_ADDR, context.getRequest().channel().remoteAddress());
		containerRequest.setEntityStream(new ByteBufInputStream(httpRequest.content()));
		
        for (Map.Entry<String, String> headerEntry : httpRequest.headers()) {
        	containerRequest.getHeaders().add(headerEntry.getKey(), headerEntry.getValue());
        }
        return containerRequest;
	}
	
	/**
	 * Creates the http response from container response</p>
	 * 
	 * <li> Sets the content length to the http response from the container response. If content length is not available, computes from the response entity
	 * <li> 
	 * 
	 * @param context
	 * @param containerResponse
	 * @param buffer
	 * @return
	 */
	protected FullHttpResponse createHttpResponse(MessageContext context, ContainerResponse containerResponse, ByteBuf buffer) {
		FullHttpRequest httpRequest = context.getRequest();
		DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(httpRequest.getProtocolVersion(), HttpResponseStatus.valueOf(containerResponse.getStatus()), buffer);
		int length = containerResponse.getLength();
		
		// FIXME Hack. is there a better way?
		if (length == -1 && containerResponse.getEntity() instanceof String) {
			final String entity = (String) containerResponse.getEntity();
			final byte[] encodedBytes = entity.getBytes(Charset.forName("UTF-8"));
			length = encodedBytes.length;
		}
		if (! containerResponse.getHeaders().containsKey(HttpHeaders.Names.CONTENT_LENGTH)) {
			HttpHeaders.setContentLength(httpResponse, containerResponse.getLength());
			logger.trace("Writing response status and headers {}, length {}", containerResponse, containerResponse.getLength());
		}

		for (Map.Entry<String, List<Object>> headerEntry : containerResponse.getHeaders().entrySet()) {
			HttpHeaders.addHeader(httpResponse, headerEntry.getKey(), Joiner.on(", ").join(headerEntry.getValue()));
		}
		return httpResponse;
	}
	
	protected void registerListener(RouterListener listener) {
		this.listener = listener;
	}
}
