/**
 * 
 */
package org.minnal.core.server;

import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ServerResponseTest {

	private HttpResponse httpResponse;
	
	@BeforeMethod
	public void setup() {
		httpResponse = mock(HttpResponse.class);
	}
	
	@Test
	public void shouldVerifyDelegation() {
		ServerResponse response = new ServerResponse(mock(ServerRequest.class), httpResponse);
		response.setContent(mock(ChannelBuffer.class));
		response.addHeader("name", "value");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("name1", "value1");
		headers.put("name2", "value2");
		response.addHeaders(headers);
		verify(httpResponse).addHeader("name", "value");
		verify(httpResponse).addHeader("name1", "value1");
		verify(httpResponse).addHeader("name2", "value2");
	}
}
