/**
 * 
 */
package org.minnal.metrics;

import java.util.concurrent.TimeUnit;

import org.minnal.core.MessageListener;
import org.minnal.core.route.Route;
import org.minnal.core.server.MessageContext;

import com.codahale.metrics.Clock;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * @author ganeshs
 *
 */
public class ResponseMetricCollector implements MessageListener {
	
	public static final String EXCEPTIONS = "exceptions";

	public static final String RESPONSE_TIME = "responseTime";

	public static final String SUCCESSFUL = "successful";
	
	public static final String START_TIME = "startTime";
	
	private Clock clock = Clock.defaultClock();
	
	@Override
	public void onReceived(MessageContext context) {
		context.addAttribute(START_TIME, clock.getTick());
	}

	@Override
	public void onApplicationResolved(MessageContext context) {
	}

	@Override
	public void onRouteResolved(MessageContext context) {
	}
	
	protected String getMetricName(MessageContext context, String metricName) {
		Route route = context.getRoute();
		String name = null;
		if (route != null) {
			name = context.getRequest().getApplicationPath() + route.getRoutePattern().getPathPattern();
		} else {
			name = context.getApplication().getConfiguration().getName();
		}
		return MetricRegistry.name(name, context.getRequest().getHttpMethod().toString(), metricName);
	}

	@Override
	public void onSuccess(MessageContext context) {
		context.addAttribute(SUCCESSFUL, context.getResponse().getStatus().getCode() < 400);
	}

	@Override
	public void onError(MessageContext context) {
	}

	@Override
	public void onComplete(MessageContext context) {
		String name = null;
		Boolean successful = (Boolean) context.getAttribute(SUCCESSFUL);
		if (successful == null) {
			name = MetricRegistry.name(context.getApplication().getConfiguration().getName(), EXCEPTIONS);
			MetricRegistries.getRegistry(context.getApplication().getConfiguration().getName()).meter(name).mark();
		} else {
			if (! successful) {
				name = getMetricName(context, Integer.toString(context.getResponse().getStatus().getCode()));
				MetricRegistries.getRegistry(context.getApplication().getConfiguration().getName()).meter(name).mark();
			}
			name = getMetricName(context, RESPONSE_TIME);
			Timer timer = MetricRegistries.getRegistry(context.getApplication().getConfiguration().getName()).timer(name);
			timer.update(clock.getTick() - (Long) context.getAttribute(START_TIME), TimeUnit.NANOSECONDS);
		}
		
		
	}
}
