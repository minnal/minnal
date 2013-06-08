/**
 * 
 */
package org.minnal.migrations.plugin;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.DatabaseConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.googlecode.flyway.core.Flyway;

/**
 * @author ganeshs
 *
 */
public class MigrationsPluginTest {
	
	private MigrationsPlugin plugin;
	
	private Application<ApplicationConfiguration> application;
	
	private Flyway flyway;
	
	private DatabaseConfiguration dbConfig;

	@BeforeMethod
	public void setup() {
		flyway = mock(Flyway.class);
		plugin = new MigrationsPlugin(flyway);
		application = mock(Application.class);
		dbConfig = mock(DatabaseConfiguration.class);
		when(dbConfig.getUrl()).thenReturn("jdbc:mysql://localhost/test");
		when(dbConfig.getUsername()).thenReturn("test");
		when(dbConfig.getPassword()).thenReturn("");
		ApplicationConfiguration appConfig = mock(ApplicationConfiguration.class);
		when(appConfig.getDatabaseConfiguration()).thenReturn(dbConfig);
		when(application.getConfiguration()).thenReturn(appConfig);
	}
	
	@Test
	public void shouldSetDataSourceOnInit() {
		plugin.init(application);
		verify(flyway).setDataSource(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword());
	}
	
	@Test
	public void shouldMigrateOnInit() {
		plugin.init(application);
		verify(flyway).migrate();
	}
}
