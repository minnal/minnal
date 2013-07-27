/**
 * 
 */
package org.minnal.metrics;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.minnal.core.Application;
import org.minnal.core.Bundle;
import org.minnal.core.Container;
import org.minnal.core.ContainerAdapter;
import org.minnal.core.config.ApplicationConfiguration;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

/**
 * @author ganeshs
 *
 */
public class MetricsBundle extends ContainerAdapter implements Bundle<MetricsBundleConfiguration> {
	
	private ResponseMetricCollector responseMetricCollector = new ResponseMetricCollector();
	
	private MetricsBundleConfiguration configuration;
	
	@Override
	public void init(Container container, MetricsBundleConfiguration configuration) {
		this.configuration = configuration;
		container.registerListener(this);
		container.registerListener(responseMetricCollector);
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public void postMount(Application<ApplicationConfiguration> application) {
		MetricRegistry registry = createMetricRegistry();
		
		if (configuration.isEnableJmxReporter()) {
			JmxReporter reporter = createJmxReporter(registry);
			reporter.start();
		}
		
		if (configuration.isEnableGraphiteReporter()) {
			GraphiteReporterConfiguration config = configuration.getGraphiteReporterConfiguration();
			GraphiteReporter reporter = createGraphiteReporter(config, registry);
			reporter.start(config.getPollPeriodInSecs(), TimeUnit.SECONDS);
		}
		
		MetricRegistries.addRegistry(application, registry);

		DataSourcePoolMetricCollector collector = createDataSourceMetricCollector(application); 
		collector.init();
	}
	
	protected DataSourcePoolMetricCollector createDataSourceMetricCollector(Application<ApplicationConfiguration> application) {
		return new DataSourcePoolMetricCollector(application);
	}

	protected MetricRegistry createMetricRegistry() {
		return new MetricRegistry();
	}
	
	protected GraphiteReporter createGraphiteReporter(GraphiteReporterConfiguration config, MetricRegistry registry) {
		Graphite graphite = new Graphite(new InetSocketAddress(config.getGraphiteHost(), config.getGraphitePort()));
		return GraphiteReporter.forRegistry(registry).prefixedWith(config.getPrefix())
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .filter(MetricFilter.ALL)
            .build(graphite);
	}
	
	protected JmxReporter createJmxReporter(MetricRegistry registry) {
		return JmxReporter.forRegistry(registry).build();
	}
	
	@Override
	public void postUnMount(Application<ApplicationConfiguration> application) {
		MetricRegistries.removeRegistry(application);
	}
}
