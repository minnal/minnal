/**
 * 
 */
package org.minnal.jpa;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.persistence.SharedCacheMode;
import javax.persistence.ValidationMode;
import javax.persistence.spi.ClassTransformer;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import javax.sql.DataSource;

import org.minnal.core.MinnalException;
import org.minnal.core.config.DatabaseConfiguration;
import org.minnal.core.scanner.Scanner.Listener;
import org.minnal.jpa.entity.EntityScanner;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * @author ganeshs
 *
 */
public class MinnalPersistenceUnitInfo implements PersistenceUnitInfo {
	
	private DatabaseConfiguration configuration;
	
	private String unitName;
	
	private PersistenceProvider provider;
	
	private Properties properties = new Properties();
	
	private DataSource dataSource;
	
	private List<String> managedClasses = new ArrayList<String>();
	
	public MinnalPersistenceUnitInfo(String unitName, DatabaseConfiguration configuration, PersistenceProvider provider) {
		this.unitName = unitName;
		this.configuration = configuration;
		this.provider = provider;
		init();
	}
	
	private void init() {
		properties.putAll(configuration.getProviderProperties());
		createDataSource();
		scanForEntities();
	}
	
	private void scanForEntities() {
		EntityScanner scanner = new EntityScanner(configuration.getPackagesToScan().toArray(new String[0]));
		scanner.scan(new Listener<Class<?>>() {
			public void handle(Class<?> entity) {
				managedClasses.add(entity.getName());
			}
		});
	}

	public String getPersistenceUnitName() {
		return unitName;
	}

	public String getPersistenceProviderClassName() {
		return provider.getClass().getName();
	}

	public PersistenceUnitTransactionType getTransactionType() {
		return PersistenceUnitTransactionType.RESOURCE_LOCAL;
	}

	public DataSource getJtaDataSource() {
		return null;
	}

	public DataSource getNonJtaDataSource() {
		return dataSource;
	}

	public List<String> getMappingFileNames() {
		return Collections.emptyList();
	}

	public List<URL> getJarFileUrls() {
		return Collections.emptyList();
	}

	public URL getPersistenceUnitRootUrl() {
		return null;
	}

	public List<String> getManagedClassNames() {
		return managedClasses;
	}

	public boolean excludeUnlistedClasses() {
		return false;
	}

	public SharedCacheMode getSharedCacheMode() {
		return SharedCacheMode.ALL;
	}

	public ValidationMode getValidationMode() {
		return ValidationMode.AUTO;
	}

	public Properties getProperties() {
		return properties;
	}

	public String getPersistenceXMLSchemaVersion() {
		return "2.0"; // Why is this required?
	}

	public ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	public void addTransformer(ClassTransformer transformer) {
		throw new UnsupportedOperationException("Not supported");
	}

	public ClassLoader getNewTempClassLoader() {
		return null;
	}
	
	public void createDataSource() {
		ComboPooledDataSource ds = new ComboPooledDataSource();
		try {
			Class.forName(configuration.getDriverClass());
			ds.setJdbcUrl(configuration.getUrl());
			ds.setUser(configuration.getUsername());
			ds.setPassword(configuration.getPassword());
			ds.setIdleConnectionTestPeriod(configuration.getIdleConnectionTestPeriod());
			ds.setInitialPoolSize(configuration.getMinSize());
			ds.setMaxPoolSize(configuration.getMaxSize());
		} catch (Exception e) {
			throw new MinnalException("Failed while configuring the data source", e);
		}
		dataSource = ds;
	}

}
