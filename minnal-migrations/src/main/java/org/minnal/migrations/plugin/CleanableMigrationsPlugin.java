/**
 * 
 */
package org.minnal.migrations.plugin;

import com.googlecode.flyway.core.Flyway;


/**
 * Cleans the objects created during migration at the time of plugin destroy
 * 
 * @author ganeshs
 *
 */
public class CleanableMigrationsPlugin extends MigrationsPlugin {
	
	public CleanableMigrationsPlugin() {
	}

	/**
	 * @param flyway
	 */
	public CleanableMigrationsPlugin(Flyway flyway) {
		super(flyway);
	}

	public void destroy() {
		flyway.clean();
	}
	
}
