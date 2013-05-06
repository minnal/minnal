/**
 * 
 */
package org.minnal.core.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
public class ContainerConfiguration extends Configuration {

	@JsonProperty("server")
	private ServerConfiguration serverConfiguration;
	
	@JsonProperty(required=true)
	private Map<String, String> mounts = new HashMap<String, String>();
	
	private String basePath = "/";
	
	public ContainerConfiguration() {
	}
	
	public ContainerConfiguration(String name) {
		super(name);
	}

	/**
	 * @return the serverConfiguration
	 */
	public ServerConfiguration getServerConfiguration() {
		return serverConfiguration;
	}

	/**
	 * @param serverConfiguration the serverConfiguration to set
	 */
	public void setServerConfiguration(ServerConfiguration serverConfiguration) {
		this.serverConfiguration = serverConfiguration;
	}

	/**
	 * @return the mounts
	 */
	public Map<String, String> getMounts() {
		return mounts;
	}

	/**
	 * @param mounts the mounts to set
	 */
	public void setMounts(Map<String, String> mounts) {
		this.mounts = mounts;
	}

	/**
	 * @return the basePath
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * @param basePath the basePath to set
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.addMixInAnnotations(MediaType.class, MediaTypeMixin.class);
		ContainerConfiguration configuration = mapper.readValue(Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/container.sample.yml"), ContainerConfiguration.class);
		System.out.println(configuration);
	}
}
