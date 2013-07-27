/**
 * 
 */
package org.minnal.metrics;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.minnal.core.Application;
import org.minnal.core.Container;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.metrics.GraphiteReporterConfiguration;
import org.minnal.metrics.MetricsBundle;
import org.minnal.metrics.MetricsBundleConfiguration;
import org.minnal.metrics.ResponseMetricCollector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.GraphiteReporter;

/**
 * @author ganeshs
 *
 */
public class MetricsBundleTest {
	
	private MetricsBundle bundle;
	
	private MetricsBundleConfiguration configuration;
	
	private Container container;
	
	private Application application;
	
	@BeforeMethod
	public void setup() {
		bundle = spy(new MetricsBundle());
		container = mock(Container.class);
		configuration = new MetricsBundleConfiguration(true, true, new GraphiteReporterConfiguration("localhost", 8080, "test123", 60));
		
		application = mock(Application.class);
		ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
		when(configuration.getName()).thenReturn("test1");
		when(application.getConfiguration()).thenReturn(configuration);
	}

	@Test
	public void shouldInitializeMetrics() {
		bundle.init(container, configuration);
		verify(container).registerListener(bundle);
		verify(container).registerListener(any(ResponseMetricCollector.class));
	}
	
	@Test
	public void shouldHandlePostMount() {
		bundle.init(container, configuration);
		
		MetricRegistry metricRegistry = mock(MetricRegistry.class);
		JmxReporter jmxReporter = mock(JmxReporter.class);
		GraphiteReporter graphiteReporter = mock(GraphiteReporter.class);
		
		doReturn(metricRegistry).when(bundle).createMetricRegistry();
		doReturn(jmxReporter).when(bundle).createJmxReporter(metricRegistry);
		doReturn(graphiteReporter).when(bundle).createGraphiteReporter(eq(configuration.getGraphiteReporterConfiguration()), eq(metricRegistry));
		
		bundle.postMount(application);
		verify(jmxReporter).start();
		verify(graphiteReporter).start(configuration.getGraphiteReporterConfiguration().getPollPeriodInSecs(), TimeUnit.SECONDS);
	}
	
	@Test
	public void shouldInitDataSourcePoolMetricCollector() {
		bundle.init(container, configuration);
		DataSourcePoolMetricCollector collector = mock(DataSourcePoolMetricCollector.class);
		doReturn(collector).when(bundle).createDataSourceMetricCollector(application);
		bundle.postMount(application);
		verify(collector).init();
	}
}
