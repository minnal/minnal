/**
 * 
 */
package org.minnal.metrics;

import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.DatabaseConfiguration;
import org.minnal.core.db.DataSourceStatistics;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;

/**
 * @author ganeshs
 * 
 */
public class DataSourcePoolMetricCollector {
	
	private Application<ApplicationConfiguration> application;
	
	private DataSourceStatistics statistics;
	
	private MetricRegistry metricRegistry;
	
	public DataSourcePoolMetricCollector(Application<ApplicationConfiguration> application) {
		this.application = application;
	}

	protected void init() {
		DatabaseConfiguration configuration = application.getConfiguration().getDatabaseConfiguration();
		if (configuration == null) {
			return;
		}
		statistics = configuration.getDataSourceProvider().getStatistics();
		if (statistics == null) {
			return;
		}
		metricRegistry = MetricRegistries.getRegistry(application.getConfiguration().getName());
		registerGuages();
	}
	
	protected void registerGuages() {
		metricRegistry.register(MetricRegistry.name(application.getConfiguration().getName(), "datasource", "activeConnections"),
            new Gauge<Integer>() {
                @Override
                public Integer getValue() {
                    return statistics.getActiveConnections();
                }
            }
		);
		
		metricRegistry.register(MetricRegistry.name(application.getConfiguration().getName(), "datasource", "awaitingCheckout"),
            new Gauge<Integer>() {
                @Override
                public Integer getValue() {
                    return statistics.getAwaitingCheckout();
                }
            }
		);
		
		metricRegistry.register(MetricRegistry.name(application.getConfiguration().getName(), "datasource", "idleConnections"),
            new Gauge<Integer>() {
                @Override
                public Integer getValue() {
                    return statistics.getIdleConnections();
                }
            }
		);
		
		metricRegistry.register(MetricRegistry.name(application.getConfiguration().getName(), "datasource", "cachedStatements"),
            new Gauge<Integer>() {
                @Override
                public Integer getValue() {
                    return statistics.getCachedStatements();
                }
            }
		);
		
		metricRegistry.register(MetricRegistry.name(application.getConfiguration().getName(), "datasource", "totalConnections"),
            new Gauge<Integer>() {
                @Override
                public Integer getValue() {
                    return statistics.getTotalConnections();
                }
            }
		);
		
		metricRegistry.register(MetricRegistry.name(application.getConfiguration().getName(), "datasource", "failedCheckins"),
            new Gauge<Long>() {
                @Override
                public Long getValue() {
                    return statistics.getFailedCheckins();
                }
            }
		);
		
		metricRegistry.register(MetricRegistry.name(application.getConfiguration().getName(), "datasource", "failedCheckouts"),
            new Gauge<Long>() {
                @Override
                public Long getValue() {
                    return statistics.getFailedCheckouts();
                }
            }
		);
		
		metricRegistry.register(MetricRegistry.name(application.getConfiguration().getName(), "datasource", "failedIdleTests"),
            new Gauge<Long>() {
                @Override
                public Long getValue() {
                    return statistics.getFailedIdleTests();
                }
            }
		);
		
		metricRegistry.register(MetricRegistry.name(application.getConfiguration().getName(), "datasource", "upTime"),
            new Gauge<Long>() {
                @Override
                public Long getValue() {
                    return statistics.getUpTime();
                }
            }
		);
	}

}
