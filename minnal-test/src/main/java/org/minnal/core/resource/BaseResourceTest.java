/**
 * 
 */
package org.minnal.core.resource;

import java.net.InetSocketAddress;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.minnal.core.Container;
import org.minnal.core.Response;
import org.minnal.core.Router;
import org.minnal.core.serializer.Serializer;
import org.minnal.core.server.MessageContext;
import org.minnal.core.server.ServerRequest;
import org.minnal.core.server.ServerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.google.common.collect.Maps;
import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
public abstract class BaseResourceTest {
	
	private static final Logger logger = LoggerFactory.getLogger(BaseResourceTest.class);
	
	static {
		Class<?> clazz = null;
		try {
			clazz = Class.forName("org.activejpa.enhancer.ActiveJpaAgentLoader");
		} catch (ClassNotFoundException e) {
			logger.debug("org.activejpa.enhancer.ActiveJpaAgentLoader is not found. Not loading the agent");
		}
		
		if (clazz != null) {
			try {
				Object instance = clazz.getMethod("instance").invoke(null);
				clazz.getMethod("loadAgent").invoke(instance);
			} catch (Exception e) {
				throw new Error("Failed while loading the activejpa agent");
			}
		}
	}
	
	private Router router;
	
	protected Serializer serializer;
	
	private static Container container = new Container();
	
	@BeforeSuite
	public void beforeSuite() {
		container.init();
		container.start();
	}
	
	@BeforeMethod
	public void beforeMethod() {
		serializer = container.getConfiguration().getSerializer(container.getConfiguration().getDefaultMediaType());
		router = container.getRouter();
		setup();
	}
	
	@AfterMethod
	public void afterMethod() {
		destroy();
	}
	
	@AfterSuite
	public void afterSuite() {
		container.stop();
	}
	
	protected void setup() {
	}
	
	protected void destroy() {
	}
	
	protected void route(MessageContext context) {
		router.route(context);
	}
	
	protected ServerRequest request(String uri, HttpMethod method, String content) {
		return request(uri, method, content, MediaType.JSON_UTF_8);
	}

	protected ServerRequest request(String uri, HttpMethod method, String content, MediaType contentType) {
		return request(uri, method, content, contentType, Maps.<String, String>newHashMap());
	}
	
	protected ServerRequest request(String uri, HttpMethod method, String content, MediaType contentType, Map<String, String> headers) {
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, method, uri);
		request.setContent(buffer(content));
		request.setHeader(HttpHeaders.Names.CONTENT_TYPE, contentType.toString());
		request.setHeader(HttpHeaders.Names.ACCEPT, MediaType.ANY_TYPE);
		ServerRequest serverRequest = new ServerRequest(request, InetSocketAddress.createUnresolved("localhost", 80));
		serverRequest.addHeaders(headers);
		return serverRequest;
	}
	
	protected ServerResponse response(ServerRequest request) {
		return new ServerResponse(request, new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.PROCESSING));
	}
	
	protected ChannelBuffer buffer(String content) {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		ChannelBufferOutputStream os = new ChannelBufferOutputStream(buffer);
		try {
			os.write(content.getBytes());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return buffer;
	}
	
	protected Response call(ServerRequest request) {
		ServerResponse response = response(request);
		MessageContext context = new MessageContext(request, response);
		route(context);
		return response;
	}
	
}
