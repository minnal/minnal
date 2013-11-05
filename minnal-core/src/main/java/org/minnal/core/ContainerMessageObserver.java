/**
 * 
 */
package org.minnal.core;

import java.util.ArrayList;
import java.util.List;

import org.minnal.core.server.ConnectorListener;
import org.minnal.core.server.MessageContext;
import org.minnal.core.server.exception.ApplicationException;
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
		for (final MessageListener listener : messageListeners) {
			invoke(new ListenerInvoker(){
				@Override
				public void invoke(MessageContext context) {
					listener.onReceived(context);
				}
			}, context);
		}
	}

	@Override
	public void onSuccess(MessageContext context) {
		for (final MessageListener listener : messageListeners) {
			invoke(new ListenerInvoker(){
				@Override
				public void invoke(MessageContext context) {
					listener.onSuccess(context);
				}
			}, context);
		}
	}

	@Override
	public void onError(MessageContext context) {
		for (final MessageListener listener : messageListeners) {
			invoke(new ListenerInvoker(){
				@Override
				public void invoke(MessageContext context) {
					listener.onError(context);
				}
			}, context);
		}
	}

	@Override
	public void onComplete(MessageContext context) {
		for (final MessageListener listener : messageListeners) {
			invoke(new ListenerInvoker(){
				@Override
				public void invoke(MessageContext context) {
					listener.onComplete(context);
				}
			}, context);
		}
	}

	@Override
	public void onApplicationResolved(MessageContext context) {
		ApplicationContext.instance().setApplicationConfiguration(context.getApplication().getConfiguration());
		for (final MessageListener listener : messageListeners) {
			invoke(new ListenerInvoker(){
				@Override
				public void invoke(MessageContext context) {
					listener.onApplicationResolved(context);
				}
			}, context);
		}
	}

	@Override
	public void onRouteResolved(MessageContext context) {
		ApplicationContext.instance().setResourceConfiguration(context.getResourceClass().getConfiguration());
		ApplicationContext.instance().setRouteConfiguration(context.getRoute().getConfiguration());
		for (final MessageListener listener : messageListeners) {
			invoke(new ListenerInvoker(){
				@Override
				public void invoke(MessageContext context) {
					listener.onRouteResolved(context);
				}
			}, context);
		}
	}
	
	protected void invoke(ListenerInvoker invoker, MessageContext context) {
		try {
			invoker.invoke(context);
		} catch (ApplicationException e) {
			throw e;
		} catch (Exception e) {
			logger.warn("Failed while handling the event", e);
			throw new MinnalException(e);
		}
	}

	@Override
	public void onError(Throwable cause) {
	}
	
	public static interface ListenerInvoker {
		
		void invoke(MessageContext context);
	}
}
