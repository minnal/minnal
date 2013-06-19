/**
 * 
 */
package org.minnal.core.db;

import javax.sql.DataSource;

import org.minnal.core.MinnalException;
import org.minnal.core.config.DatabaseConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.PooledDataSource;

/**
 * @author ganeshs
 *
 */
public class C3P0DataSourceProvider implements DataSourceProvider {
	
	private DatabaseConfiguration configuration;
	
	private PooledDataSource dataSource;
	
	private DataSourceStatistics statistics;
	
	private static final Logger logger = LoggerFactory.getLogger(C3P0DataSourceProvider.class);
	
	public C3P0DataSourceProvider() {
	}
	
	public C3P0DataSourceProvider(DatabaseConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public DataSource getDataSource() {
		if (dataSource == null) {
			dataSource = createDataSource();
			statistics = new C3P0DataSourceStatistics(dataSource);
		}
		return dataSource;
	}
	
	protected PooledDataSource createDataSource() {
		logger.info("Creating the data source with the configuration {}", configuration);
		if (configuration == null) {
			logger.error("Database configuration is not set");
			throw new MinnalException("Database configuration is not set");
		}
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			Class.forName(configuration.getDriverClass());
			dataSource.setJdbcUrl(configuration.getUrl());
			dataSource.setUser(configuration.getUsername());
			dataSource.setPassword(configuration.getPassword());
			dataSource.setIdleConnectionTestPeriod(configuration.getIdleConnectionTestPeriod());
			dataSource.setInitialPoolSize(configuration.getMinSize());
			dataSource.setMaxPoolSize(configuration.getMaxSize());
		} catch (Exception e) {
			logger.error("Failed while creating the data source", e);
			throw new MinnalException("Failed while configuring the data source", e);
		}
		return dataSource;
	}

	@Override
	public void close() {
		try {
			logger.info("Closing the datasource");
			dataSource.close();
		} catch (Exception e) {
			logger.error("Error while closing the datasource", e);
		}
	}

	@Override
	public DataSourceStatistics getStatistics() {
		return statistics;
	}

	/**
	 * @return the configuration
	 */
	public DatabaseConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(DatabaseConfiguration configuration) {
		this.configuration = configuration;
	}
}
