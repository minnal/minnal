/**
 * 
 */
package org.minnal.core.db;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.minnal.core.config.DatabaseConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mchange.v2.c3p0.PooledDataSource;

/**
 * @author ganeshs
 *
 */
public class C3P0DataSourceProviderTest {

	private C3P0DataSourceProvider provider;
	
	private DatabaseConfiguration configuration;
	
	@BeforeMethod
	public void beforeMethod() {
		configuration = new DatabaseConfiguration();
		configuration.setDriverClass("org.hsqldb.jdbcDriver");
		configuration.setUrl("jdbc:hsqldb:mem:.");
		configuration.setUsername("sa");
		provider = spy(new C3P0DataSourceProvider(configuration));
	}
	
	@Test
	public void shouldCreateDataSource() {
		assertNotNull(provider.createDataSource());
	}
	
	@Test
	public void shouldCreateDataSourceLazilyOnGet() {
		PooledDataSource dataSource = mock(PooledDataSource.class);
		doReturn(dataSource).when(provider).createDataSource();
		assertEquals(provider.getDataSource(), dataSource);
	}
	
	@Test
	public void shouldGetStatistics() {
		PooledDataSource dataSource = mock(PooledDataSource.class);
		doReturn(dataSource).when(provider).createDataSource();
		assertEquals(provider.getDataSource(), dataSource);
		assertEquals(provider.getStatistics(), new C3P0DataSourceStatistics(dataSource));
	}
}
