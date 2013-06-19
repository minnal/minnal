/**
 * 
 */
package org.minnal.core.db;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ganeshs
 *
 */
public interface DataSourceStatistics {

	@JsonProperty
	int getActiveConnections();
	
	@JsonProperty
	int getIdleConnections();
	
	@JsonProperty
	long getFailedCheckins();
	
	@JsonProperty
	long getFailedCheckouts();
	
	@JsonProperty
	int getAwaitingCheckout();
	
	@JsonProperty
	long getFailedIdleTests();
	
	@JsonProperty
	int getTotalConnections();
	
	@JsonProperty
	int getCachedStatements();
	
	@JsonProperty
	long getStartTime();
	
	@JsonProperty
	long getUpTime();
}
