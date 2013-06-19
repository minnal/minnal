/**
 * 
 */
package org.minnal.core.db;

import java.sql.SQLException;

import org.minnal.core.MinnalException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mchange.v2.c3p0.PooledDataSource;

/**
 * @author ganeshs
 *
 */
public class C3P0DataSourceStatistics implements DataSourceStatistics {
	
	@JsonIgnore
	private PooledDataSource dataSource;
	
	/**
	 * @param dataSource
	 */
	public C3P0DataSourceStatistics(PooledDataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public int getActiveConnections() {
		try {
			return dataSource.getNumBusyConnectionsAllUsers();
		} catch (SQLException e) {
			throw new MinnalException(e);
		}
	}

	@Override
	public int getIdleConnections() {
		try {
			return dataSource.getNumIdleConnectionsAllUsers();
		} catch (SQLException e) {
			throw new MinnalException(e);
		}
	}

	@Override
	public long getFailedCheckins() {
		try {
			return dataSource.getNumFailedCheckinsDefaultUser();
		} catch (SQLException e) {
			throw new MinnalException(e);
		}
	}

	@Override
	public long getFailedCheckouts() {
		try {
			return dataSource.getNumFailedCheckoutsDefaultUser();
		} catch (SQLException e) {
			throw new MinnalException(e);
		}
	}

	@Override
	public int getAwaitingCheckout() {
		try {
			return dataSource.getNumThreadsAwaitingCheckoutDefaultUser();
		} catch (SQLException e) {
			throw new MinnalException(e);
		}
	}

	@Override
	public long getFailedIdleTests() {
		try {
			return dataSource.getNumFailedIdleTestsDefaultUser();
		} catch (SQLException e) {
			throw new MinnalException(e);
		}
	}

	@Override
	public int getTotalConnections() {
		try {
			return dataSource.getNumConnectionsAllUsers();
		} catch (SQLException e) {
			throw new MinnalException(e);
		}
	}

	@Override
	public int getCachedStatements() {
		try {
			return dataSource.getStatementCacheNumStatementsAllUsers();
		} catch (SQLException e) {
			throw new MinnalException(e);
		}
	}

	@Override
	public long getStartTime() {
		try {
			return dataSource.getStartTimeMillisDefaultUser();
		} catch (SQLException e) {
			throw new MinnalException(e);
		}
	}

	@Override
	public long getUpTime() {
		try {
			return dataSource.getUpTimeMillisDefaultUser();
		} catch (SQLException e) {
			throw new MinnalException(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataSource == null) ? 0 : dataSource.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		C3P0DataSourceStatistics other = (C3P0DataSourceStatistics) obj;
		if (dataSource == null) {
			if (other.dataSource != null)
				return false;
		} else if (!dataSource.equals(other.dataSource))
			return false;
		return true;
	}
}
