/**
 * 
 */
package org.minnal.jpa;

import java.net.URL;
import java.security.CodeSource;
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

import org.minnal.core.config.DatabaseConfiguration;
import org.minnal.jpa.entity.ConverterScanner;
import org.minnal.jpa.entity.EntityScanner;
import org.minnal.utils.scanner.Scanner;
import org.minnal.utils.scanner.Scanner.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static final Logger logger = LoggerFactory.getLogger(MinnalPersistenceUnitInfo.class);
	
	public MinnalPersistenceUnitInfo(String unitName, DatabaseConfiguration configuration, PersistenceProvider provider) {
		this.unitName = unitName;
		this.configuration = configuration;
		this.provider = provider;
		init();
	}
	
	private void init() {
		properties.putAll(configuration.getProviderProperties());
		dataSource = configuration.getDataSourceProvider().getDataSource();
		String[] packagesToScan = configuration.getPackagesToScan().toArray(new String[0]);
		scanForManagedClasses(new ConverterScanner(packagesToScan));
		scanForManagedClasses(new EntityScanner(packagesToScan));
	}
	
	private void scanForManagedClasses(Scanner<Class<?>> scanner) {
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
		// No need to specify the persistence unit root url as we do it programmatically. But eclipselink expects it, 
		// although it doesn't load the file, returning null causes a NPE with eclipselink. So returning a dummy url from here.
		CodeSource codeSource = getClass().getProtectionDomain().getCodeSource();
		return codeSource.getLocation();
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
		logger.info("Add transformer called on the persistent unit info. Ignoring");
		// TODO Support adding transformers.
		// Ignore this for now
	}

	public ClassLoader getNewTempClassLoader() {
		return null;
	}

}
