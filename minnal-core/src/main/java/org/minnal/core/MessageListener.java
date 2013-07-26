/**
 * 
 */
package org.minnal.core;

import org.minnal.core.server.MessageContext;

/**
 * @author ganeshs
 *
 */
public interface MessageListener {

	void onReceived(MessageContext context);
	
	void onApplicationResolved(MessageContext context);
	
	void onRouteResolved(MessageContext context);
	
	void onSuccess(MessageContext context);
	
	void onError(MessageContext context);
	
	void onComplete(MessageContext context);
}
