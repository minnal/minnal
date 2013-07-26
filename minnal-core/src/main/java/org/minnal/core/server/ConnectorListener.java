/**
 * 
 */
package org.minnal.core.server;

/**
 * @author ganeshs
 *
 */
public interface ConnectorListener {

	void onReceived(MessageContext context);
	
	void onSuccess(MessageContext context);
	
	void onError(MessageContext context);
	
	void onError(Throwable cause);
	
	void onComplete(MessageContext context);
}
