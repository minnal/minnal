/**
 * 
 */
package org.minnal.metrics;

import java.util.concurrent.TimeUnit;

import org.minnal.core.MessageListenerAdapter;
import org.minnal.core.server.MessageContext;

import com.codahale.metrics.Clock;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.CharMatcher;

/**
 * @author ganeshs
 *
 */
public class ResponseMetricCollector extends MessageListenerAdapter {
	
	public static final String EXCEPTIONS = "exceptions";

	public static final String RESPONSE_TIME = "responseTime";

	public static final String SUCCESSFUL = "successful";
	
	public static final String START_TIME = "startTime";
	
	private Clock clock = Clock.defaultClock();
	
	@Override
	public void onReceived(MessageContext context) {
		context.addAttribute(START_TIME, clock.getTick());
	}

	private String formatName(String name){
		String slashRemoved = CharMatcher.anyOf("/").trimAndCollapseFrom(name, '.');
		return CharMatcher.anyOf("{}").removeFrom(slashRemoved);
	}
	
	protected String getMetricName(MessageContext context, String metricName) {
		Route route = context.getRoute();
		String name = null;
		if (route != null) {
			name = context.getRequest().getApplicationPath() + route.getRoutePattern().getPathPattern();
		} else {
			name = context.getApplication().getConfiguration().getName();
		}
		name = formatName(name);
		return MetricRegistry.name(name, context.getRequest().getMethod().toString(), metricName);
	}

	@Override
	public void onSuccess(MessageContext context) {
		context.addAttribute(SUCCESSFUL, context.getResponse().getStatus().code() < 400);
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
				name = getMetricName(context, Integer.toString(context.getResponse().getStatus().code()));
				MetricRegistries.getRegistry(context.getApplication().getConfiguration().getName()).meter(name).mark();
			}
			name = getMetricName(context, RESPONSE_TIME);
			Timer timer = MetricRegistries.getRegistry(context.getApplication().getConfiguration().getName()).timer(name);
			timer.update(clock.getTick() - (Long) context.getAttribute(START_TIME), TimeUnit.NANOSECONDS);
		}
	}
}
