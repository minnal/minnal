/**
 * 
 */
package org.minnal.metrics;

import org.minnal.core.BundleConfiguration;

/**
 * @author ganeshs
 *
 */
public class MetricsBundleConfiguration extends BundleConfiguration {

	private boolean enableJmxReporter;
	
	private boolean enableGraphiteReporter;
	
	private GraphiteReporterConfiguration graphiteReporterConfiguration;
	
	public MetricsBundleConfiguration() {
	}

	/**
	 * @param enableJmxReporter
	 * @param enableGraphiteReporter
	 * @param graphiteReporterConfiguration
	 */
	public MetricsBundleConfiguration(boolean enableJmxReporter,
			boolean enableGraphiteReporter,
			GraphiteReporterConfiguration graphiteReporterConfiguration) {
		this.enableJmxReporter = enableJmxReporter;
		this.enableGraphiteReporter = enableGraphiteReporter;
		this.graphiteReporterConfiguration = graphiteReporterConfiguration;
	}

	/**
	 * @return the enableJmxReporter
	 */
	public boolean isEnableJmxReporter() {
		return enableJmxReporter;
	}

	/**
	 * @param enableJmxReporter the enableJmxReporter to set
	 */
	public void setEnableJmxReporter(boolean enableJmxReporter) {
		this.enableJmxReporter = enableJmxReporter;
	}

	/**
	 * @return the enableGraphiteReporter
	 */
	public boolean isEnableGraphiteReporter() {
		return enableGraphiteReporter;
	}

	/**
	 * @param enableGraphiteReporter the enableGraphiteReporter to set
	 */
	public void setEnableGraphiteReporter(boolean enableStatsdReporter) {
		this.enableGraphiteReporter = enableStatsdReporter;
	}

	/**
	 * @return the graphiteReporterConfiguration
	 */
	public GraphiteReporterConfiguration getGraphiteReporterConfiguration() {
		return graphiteReporterConfiguration;
	}

	/**
	 * @param graphiteReporterConfiguration the graphiteReporterConfiguration to set
	 */
	public void setGraphiteReporterConfiguration(
			GraphiteReporterConfiguration graphiteReporterConfiguration) {
		this.graphiteReporterConfiguration = graphiteReporterConfiguration;
	}

}
