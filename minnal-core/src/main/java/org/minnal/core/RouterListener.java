/**
 * 
 */
package org.minnal.core;

import org.minnal.core.server.MessageContext;

/**
 * @author ganeshs
 *
 */
public interface RouterListener {

	void onApplicationResolved(MessageContext context);
	
	void onRouteResolved(MessageContext context);
}
