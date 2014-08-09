/**
 * 
 */
package org.minnal.jpa;

import javax.ws.rs.container.PreMatching;

import org.activejpa.jpa.JPA;
import org.activejpa.jpa.JPAContext;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.minnal.core.config.DatabaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ganeshs
 *
 */
@PreMatching
public class OpenSessionInViewFilter implements RequestEventListener {
	
	private DatabaseConfiguration configuration;
	
	private ThreadLocal<Boolean> contextCreated = new ThreadLocal<Boolean>();
	
	private static final Logger logger = LoggerFactory.getLogger(OpenSessionInViewFilter.class);
	
	public OpenSessionInViewFilter(DatabaseConfiguration configuration) {
		this.configuration = configuration;
	}

	protected JPAContext getContext() {
		return JPA.instance.getDefaultConfig().getContext(configuration.isReadOnly());
	}

	protected void requestReceived(ContainerRequest request) {
		JPAContext context = getContext();
		context.getEntityManager();
		contextCreated.set(true);
	}
	
	protected void requestCompleted(ContainerRequest request, ContainerResponse response) {
		if (contextCreated.get() == null) {
			return;
		}
		contextCreated.remove();
		JPAContext context = getContext();
		if (context.isTxnOpen()) {
			context.closeTxn(true);
		}
		context.close();
	}

	@Override
	public void onEvent(RequestEvent event) {
		logger.trace("Received the event {}", event);
		switch (event.getType()) {
		case REQUEST_MATCHED:
			requestReceived(event.getContainerRequest());
			break;
		case FINISHED:
			requestCompleted(event.getContainerRequest(), event.getContainerResponse());
			break;
		default:
			break;
		}
	}

}
