/**
 * 
 */
package org.minnal.core.server.exception;

import java.util.List;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author ganeshs
 *
 */
public class UnsupportedMediaTypeException extends ApplicationException {

	private static final long serialVersionUID = 1L;
	
	public UnsupportedMediaTypeException(List<String> expectedTypes) {
		super(HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE, createMessage(expectedTypes));
	}
	
	public UnsupportedMediaTypeException(List<String> expectedTypes, String message) {
		super(HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE, createMessage(expectedTypes) + ". " + message);
	}
	
	public UnsupportedMediaTypeException(List<String> expectedTypes, String message, Throwable throwable) {
		super(HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE, createMessage(expectedTypes) + ". " + message, throwable);
	}
	
	public UnsupportedMediaTypeException(List<String> expectedTypes, Throwable throwable) {
		super(HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE, createMessage(expectedTypes),  throwable);
	}
	
	private static String createMessage(List<String> expectedTypes) {
		return HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE.getReasonPhrase() + ": Expected " + expectedTypes;
	}

}
