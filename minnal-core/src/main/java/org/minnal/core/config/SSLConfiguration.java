/**
 * 
 */
package org.minnal.core.config;

/**
 * @author ganeshs
 *
 */
public class SSLConfiguration {

	private String keystoreType = "JKS";
	
	private String keyStoreFile;
	
	private String keyPassword = "secret";
	
	private String keyStorePassword = "secret";
	
	private String protocol = "TLS";
	
	public SSLConfiguration() {
	}

	/**
	 * @param keystoreType
	 * @param keyStoreFile
	 * @param keyPassword
	 * @param protocol
	 * @param sslPort
	 */
	public SSLConfiguration(String keystoreType, String keyStoreFile,
			String keyPassword, String protocol) {
		this.keystoreType = keystoreType;
		this.keyStoreFile = keyStoreFile;
		this.keyPassword = keyPassword;
		this.protocol = protocol;
	}

	/**
	 * @return the keystoreType
	 */
	public String getKeystoreType() {
		return keystoreType;
	}

	/**
	 * @param keystoreType the keystoreType to set
	 */
	public void setKeystoreType(String keystoreType) {
		this.keystoreType = keystoreType;
	}

	/**
	 * @return the keyStoreFile
	 */
	public String getKeyStoreFile() {
		return keyStoreFile;
	}

	/**
	 * @param keyStoreFile the keyStoreFile to set
	 */
	public void setKeyStoreFile(String keyStoreFile) {
		this.keyStoreFile = keyStoreFile;
	}

	/**
	 * @return the keyPassword
	 */
	public String getKeyPassword() {
		return keyPassword;
	}

	/**
	 * @param keyPassword the keyPassword to set
	 */
	public void setKeyPassword(String keyPassword) {
		this.keyPassword = keyPassword;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * @return the keyStorePassword
	 */
	public String getKeyStorePassword() {
		return keyStorePassword;
	}

	/**
	 * @param keyStorePassword the keyStorePassword to set
	 */
	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

}
