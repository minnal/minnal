/**
 * 
 */
package org.minnal.metrics;

/**
 * @author ganeshs
 *
 */
public class GraphiteReporterConfiguration {

	private String graphiteHost;
	
	private int graphitePort;
	
	private String prefix;
	
	private int pollPeriodInSecs;
	
	public GraphiteReporterConfiguration() {
	}

	/**
	 * @param graphiteHost
	 * @param graphitePort
	 * @param prefix
	 * @param pollPeriodInSecs
	 */
	public GraphiteReporterConfiguration(String graphiteHost, int graphitePort,
			String prefix, int pollPeriodInSecs) {
		this.graphiteHost = graphiteHost;
		this.graphitePort = graphitePort;
		this.prefix = prefix;
		this.pollPeriodInSecs = pollPeriodInSecs;
	}

	/**
	 * @return the graphiteHost
	 */
	public String getGraphiteHost() {
		return graphiteHost;
	}

	/**
	 * @param graphiteHost the graphiteHost to set
	 */
	public void setGraphiteHost(String statsdHost) {
		this.graphiteHost = statsdHost;
	}

	/**
	 * @return the graphitePort
	 */
	public int getGraphitePort() {
		return graphitePort;
	}

	/**
	 * @param graphitePort the graphitePort to set
	 */
	public void setGraphitePort(int statsdPort) {
		this.graphitePort = statsdPort;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * @return the pollPeriodInSecs
	 */
	public int getPollPeriodInSecs() {
		return pollPeriodInSecs;
	}

	/**
	 * @param pollPeriodInSecs the pollPeriodInSecs to set
	 */
	public void setPollPeriodInSecs(int pollPeriodInSecs) {
		this.pollPeriodInSecs = pollPeriodInSecs;
	}
}
