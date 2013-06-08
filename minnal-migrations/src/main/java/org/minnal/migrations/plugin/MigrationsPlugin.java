/**
 * 
 */
package org.minnal.migrations.plugin;

import org.minnal.core.Application;
import org.minnal.core.Plugin;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.DatabaseConfiguration;

import com.googlecode.flyway.core.Flyway;

/**
 * @author ganeshs
 *
 */
public class MigrationsPlugin implements Plugin {
	
	private Flyway flyway;
	
	public MigrationsPlugin() {
		flyway = new Flyway();
	}
	
	public MigrationsPlugin(Flyway flyway) {
		this.flyway = flyway;
	}

	public void init(Application<? extends ApplicationConfiguration> application) {
		DatabaseConfiguration dbConfig = application.getConfiguration().getDatabaseConfiguration();
		
		flyway.setDataSource(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
		flyway.migrate();
	}

	public void destroy() {
	}
}
