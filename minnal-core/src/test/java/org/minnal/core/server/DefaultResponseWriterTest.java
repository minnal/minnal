/**
 * 
 */
package org.minnal.core.server;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.minnal.core.serializer.Serializer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.net.MediaType;

/**
 * @author anand.karthik
 *
 */
public class DefaultResponseWriterTest {

	private DefaultResponseWriter defaultResponseWriter;
	private ServerResponse serverResponse;
	private Serializer serializer;
	private ServerRequest serverRequest;
	
	@BeforeMethod
	public void setup() {
		serverResponse = mock(ServerResponse.class);
		serverRequest = mock(ServerRequest.class);
		when(serverResponse.getRequest()).thenReturn(serverRequest);
		serializer = mock(Serializer.class);
	}
	
	@Test
	public void shouldHandleNullHeaderParams(){
		when(serverResponse.getSerializer(any(MediaType.class))).thenReturn(serializer);
		when(serverRequest.getHeader(any(String.class))).thenReturn(null);
		when(serverResponse.getPrefferedContentType()).thenReturn(MediaType.JSON_UTF_8);
		defaultResponseWriter = new DefaultResponseWriter(serverResponse);
		Object value = new Object();
		defaultResponseWriter.write(value);
		verify(serializer).serialize(eq(value), any(Set.class), any(Set.class));
	}

}
