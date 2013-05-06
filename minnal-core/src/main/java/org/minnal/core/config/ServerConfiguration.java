/**
 * 
 */
package org.minnal.core.config;

/**
 * @author ganeshs
 *
 */
public class ServerConfiguration {

	private int ioWorkerThreadCount;
	
	private int httpPort = 3000;
	
	public ServerConfiguration() {
	}

	public ServerConfiguration(int httpPort, int ioWorkerThreadCount) {
		this.ioWorkerThreadCount = ioWorkerThreadCount;
		this.httpPort = httpPort;
	}

	/**
	 * @return the httpPort
	 */
	public int getHttpPort() {
		return httpPort;
	}

	/**
	 * @param httpPort the httpPort to set
	 */
	public void setHttpPort(int httpPort) {
		this.httpPort = httpPort;
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
}
