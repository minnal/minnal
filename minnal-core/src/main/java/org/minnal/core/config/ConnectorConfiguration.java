/**
 * 
 */
package org.minnal.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ganeshs
 *
 */
public class ConnectorConfiguration {

	public enum Scheme {
		http, https
	}

	private int port;
	
	private Scheme scheme = Scheme.http;
	
	@JsonProperty("ssl")
	private SSLConfiguration sslConfiguration;
	
	private int ioWorkerThreadCount;
	
	private int executorThreadCount = 2;

    private int maxContentLength = 65536;
	
	public ConnectorConfiguration() {
	}

	/**
	 * @param port
	 * @param scheme
	 * @param sslConfiguration
	 * @param ioWorkerThreadCount
	 */

    public ConnectorConfiguration(int port, Scheme scheme,
                                  SSLConfiguration configuration, int ioWorkerThreadCount) {
        this.port = port;
        this.scheme = scheme;
        this.sslConfiguration = configuration;
        this.ioWorkerThreadCount = ioWorkerThreadCount;
    }

	public ConnectorConfiguration(int port, Scheme scheme,
			SSLConfiguration configuration, int ioWorkerThreadCount, int maxContentLength) {
		this.port = port;
		this.scheme = scheme;
		this.sslConfiguration = configuration;
		this.ioWorkerThreadCount = ioWorkerThreadCount;
        this.maxContentLength = maxContentLength;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the scheme
	 */
	public Scheme getScheme() {
		return scheme;
	}

	/**
	 * @param scheme the scheme to set
	 */
	public void setScheme(Scheme scheme) {
		this.scheme = scheme;
	}

	/**
	 * @return the sslConfiguration
	 */
	public SSLConfiguration getSslConfiguration() {
		return sslConfiguration;
	}

	/**
	 * @param sslConfiguration the sslConfiguration to set
	 */
	public void setSslConfiguration(SSLConfiguration configuration) {
		this.sslConfiguration = configuration;
	}

	/**
	 * @return the ioWorkerThreadCount
	 */
	public int getIoWorkerThreadCount() {
		return ioWorkerThreadCount;
	}

	/**
	 * @param ioWorkerThreadCount the ioWorkerThreadCount to set
	 */
	public void setIoWorkerThreadCount(int ioWorkerThreadCount) {
		this.ioWorkerThreadCount = ioWorkerThreadCount;
	}

	/**
	 * @return the executorThreadCount
	 */
	public int getExecutorThreadCount() {
		return executorThreadCount;
	}

	/**
	 * @param executorThreadCount the executorThreadCount to set
	 */
	public void setExecutorThreadCount(int executorThreadCount) {
		this.executorThreadCount = executorThreadCount;
	}

    /**
     *
     * @return the maxContentLength
     */
    public int getMaxContentLength() {
        return maxContentLength;
    }

    /**
     *
     * @param maxContentLength the maxContentLength to set
     */
    public void setMaxContentLength(int maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

}
