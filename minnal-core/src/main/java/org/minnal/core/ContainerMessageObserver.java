/**
 * 
 */
package org.minnal.core;

import java.util.ArrayList;
import java.util.List;

import org.minnal.core.server.ConnectorListener;
import org.minnal.core.server.MessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class ContainerMessageObserver implements RouterListener, ConnectorListener {

	private List<MessageListener> messageListeners = new ArrayList<MessageListener>();
	
	private static final Logger logger = LoggerFactory.getLogger(ContainerMessageObserver.class);
	
	/**
	 * Registers the message listener
	 * 
	 * @param listener
	 */
	public void registerListener(MessageListener listener) {
		messageListeners.add(listener);
	}

	@Override
	public void onReceived(MessageContext context) {
		for (MessageListener listener : messageListeners) {
			try {
				listener.onReceived(context);
			} catch (Exception e) {
				logger.warn("Failed while handling the message received event", e);
			}
		}
	}

	@Override
	public void onSuccess(MessageContext context) {
		for (MessageListener listener : messageListeners) {
			try {
				listener.onSuccess(context);
			} catch (Exception e) {
				logger.warn("Failed while handling the message success event", e);
			}
		}
	}

	@Override
	public void onError(MessageContext context) {
		for (MessageListener listener : messageListeners) {
			try {
				listener.onError(context);
			} catch (Exception e) {
				logger.warn("Failed while handling the message errored event", e);
			}
		}
	}

	@Override
	public void onComplete(MessageContext context) {
		for (MessageListener listener : messageListeners) {
			try {
				listener.onComplete(context);
			} catch (Exception e) {
				logger.warn("Failed while handling the message completed event", e);
			}
		}
	}

	@Override
	public void onApplicationResolved(MessageContext context) {
		ApplicationContext.instance().setApplicationConfiguration(context.getApplication().getConfiguration());
		for (MessageListener listener : messageListeners) {
			try {
				listener.onApplicationResolved(context);
			} catch (Exception e) {
				logger.warn("Failed while handling the application resolved event", e);
			}
		}
	}

	@Override
	public void onRouteResolved(MessageContext context) {
		ApplicationContext.instance().setResourceConfiguration(context.getResourceClass().getConfiguration());
		ApplicationContext.instance().setRouteConfiguration(context.getRoute().getConfiguration());
		for (MessageListener listener : messageListeners) {
			try {
				listener.onRouteResolved(context);
			} catch (Exception e) {
				logger.warn("Failed while handling the route resolved event", e);
			}
		}
	}

	@Override
	public void onError(Throwable cause) {
		
	}
}
