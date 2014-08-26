/**
 * 
 */
package org.minnal.migrations.plugin;

import java.net.URI;

import org.minnal.core.Application;
import org.minnal.core.MinnalException;
import org.minnal.core.Plugin;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.DatabaseConfiguration;

import com.google.common.base.Strings;
import com.googlecode.flyway.core.Flyway;

/**
 * @author ganeshs
 *
 */
public class MigrationsPlugin implements Plugin {
	
	protected Flyway flyway;
	
	public MigrationsPlugin() {
		this(new Flyway());
	}
	
	public MigrationsPlugin(Flyway flyway) {
		this.flyway = flyway;
		this.flyway.setInitOnMigrate(true);
		this.flyway.setSchemas("PUBLIC");
	}

	public void init(Application<? extends ApplicationConfiguration> application) {
		DatabaseConfiguration dbConfig = application.getConfiguration().getDatabaseConfiguration();
		
		URI uri = getUri(dbConfig.getUrl());
		if (Strings.isNullOrEmpty(uri.getPath())) {
			flyway.setDataSource(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
		} else {
			flyway.setDataSource(getUrl(uri, false), dbConfig.getUsername(), dbConfig.getPassword());
			flyway.setSchemas(uri.getPath().substring(1));
		}
		flyway.migrate();
	}

	public void destroy() {
	}
	
	private URI getUri(String jdbcUrl) {
		if (Strings.isNullOrEmpty(jdbcUrl)) {
			throw new MinnalException("Invalid jdbc url");
		}
		String cleanURI = jdbcUrl.substring(5);
		try {
			return new URI(cleanURI);
		} catch (Exception e) {
			throw new MinnalException("Invalid jdbc url");
		}
	}
	
	private String getUrl(URI uri, boolean includeSchema) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("jdbc:");
		buffer.append(uri.getScheme());
		buffer.append(":");
		if (! Strings.isNullOrEmpty(uri.getHost())) {
			buffer.append("//").append(uri.getHost());
		}
		if (uri.getPort() > 0) {
			buffer.append(":").append(uri.getPort());
		}
		if (!Strings.isNullOrEmpty(uri.getPath()) && includeSchema) {
			buffer.append(uri.getPath());
		}
		if (Strings.isNullOrEmpty(uri.getPath())) {
			buffer.append(".");
		}
		return buffer.toString();
	}
}
