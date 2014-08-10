/**
 * 
 */
package org.minnal.metrics;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.DatabaseConfiguration;
import org.minnal.core.db.DataSourceProvider;
import org.minnal.core.db.DataSourceStatistics;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class DataSourcePoolMetricCollectorTest {

	private DataSourcePoolMetricCollector collector;
	
	private Application<ApplicationConfiguration> application;
	
	@BeforeMethod
	public void setup() {
		application = mock(Application.class);
		ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
		when(configuration.getName()).thenReturn("testname1");
		when(application.getConfiguration()).thenReturn(configuration);
		collector = spy(new DataSourcePoolMetricCollector(application));
	}
	
	@Test
	public void shouldNotRegisterGuagesIfApplicationDoesntUseDatasource() {
		collector.init();
		verify(collector, never()).registerGuages();
	}
	
	@Test
	public void shouldNotRegisterGuagesIfDatasourceStatisticsNotFound() {
		DataSourceProvider provider = mock(DataSourceProvider.class);
		DatabaseConfiguration configuration = mock(DatabaseConfiguration.class);
		when(configuration.getDataSourceProvider()).thenReturn(provider);
		ApplicationConfiguration applicationConfig = application.getConfiguration();
		when(applicationConfig.getDatabaseConfiguration()).thenReturn(configuration);
		collector.init();
		verify(collector, never()).registerGuages();
	}
	
	@Test
	public void shouldRegisterGuages() {
		DataSourceStatistics statistics = mock(DataSourceStatistics.class);
		DataSourceProvider provider = mock(DataSourceProvider.class);
		when(provider.getStatistics()).thenReturn(statistics);
		DatabaseConfiguration configuration = mock(DatabaseConfiguration.class);
		when(configuration.getDataSourceProvider()).thenReturn(provider);
		ApplicationConfiguration applicationConfig = application.getConfiguration();
		when(applicationConfig.getDatabaseConfiguration()).thenReturn(configuration);
		
		doNothing().when(collector).registerGuages();
		collector.init();
		verify(collector).registerGuages();
	}
}
