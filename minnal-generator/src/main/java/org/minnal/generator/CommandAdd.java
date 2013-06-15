/**
 * 
 */
package org.minnal.generator;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * @author ganeshs
 *
 */
@Parameters(separators = "=", commandDescription = "Add a bundle/plugin")
public class CommandAdd implements Command {

	@Parameter(names = "bundle", description = "The name of the bundle")
	private String bundle;

	@Parameter(names = "plugin", description = "The name of the plugin")
	private String plugin;
	
	/**
	 * @return the bundle
	 */
	public String getBundle() {
		return bundle;
	}

	/**
	 * @param bundle the bundle to set
	 */
	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	/**
	 * @return the plugin
	 */
	public String getPlugin() {
		return plugin;
	}

	/**
	 * @param plugin the plugin to set
	 */
	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void execute() {
		
	}
}
