/**
 * 
 */
package org.minnal.jpa;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.Arrays;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;

import org.activejpa.jpa.JPA;
import org.minnal.core.Application;
import org.minnal.core.MinnalException;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.DatabaseConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class JPAPluginTest {
	
	private JPAPlugin plugin;
	
	private PersistenceProvider provider;
	
	private PersistenceUnitInfo persistenceUnitInfo;
	
	private Application<ApplicationConfiguration> application;

	@BeforeMethod
	public void setup() {
		plugin = spy(new JPAPlugin());
		provider = mock(PersistenceProvider.class);
		persistenceUnitInfo = mock(PersistenceUnitInfo.class);
		application = mock(Application.class);
		ApplicationConfiguration configuration = mock(ApplicationConfiguration.class);
		when(configuration.getName()).thenReturn("test");
		when(configuration.getDatabaseConfiguration()).thenReturn(mock(DatabaseConfiguration.class));
		when(application.getConfiguration()).thenReturn(configuration);
		
		doReturn(persistenceUnitInfo).when(plugin).createPersistenceUnitInfo(configuration, provider);
		doReturn(Arrays.asList(provider)).when(plugin).getProviders();
	}
	
	@Test
	public void shouldInitPlugin() {
		EntityManagerFactory factory = mock(EntityManagerFactory.class);
		when(provider.createContainerEntityManagerFactory(persistenceUnitInfo, null)).thenReturn(factory);
		plugin.init(application);
		assertEquals(JPA.instance.getDefaultConfig().getEntityManagerFactory(), factory);
	}
	
	@Test(expectedExceptions=MinnalException.class, expectedExceptionsMessageRegExp="No JPA persistence provider found")
	public void shouldFailInitIfNoPersistenceProviderIsFound() {
		doReturn(Lists.newArrayList()).when(plugin).getProviders();
		plugin.init(application);
	}
	
	@Test
	public void shouldDestroyPlugin() {
		EntityManagerFactory factory = mock(EntityManagerFactory.class);
		when(provider.createContainerEntityManagerFactory(persistenceUnitInfo, null)).thenReturn(factory);
		plugin.init(application);
		plugin.destroy();
		verify(factory).close();
	}
}
