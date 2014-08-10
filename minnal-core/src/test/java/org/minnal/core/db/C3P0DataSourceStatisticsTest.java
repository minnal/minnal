/**
 * 
 */
package org.minnal.core.db;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.sql.SQLException;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.mchange.v2.c3p0.PooledDataSource;

/**
 * @author ganeshs
 *
 */
public class C3P0DataSourceStatisticsTest {

	private C3P0DataSourceStatistics statistics;
	
	private PooledDataSource dataSource;
	
	@BeforeMethod
	public void beforeMethod() {
		dataSource = mock(PooledDataSource.class);
		statistics = new C3P0DataSourceStatistics(dataSource);
	}
	
	@Test
	public void shouldGetTotalConnections() throws SQLException {
		when(dataSource.getNumConnectionsAllUsers()).thenReturn(10);
		assertEquals(statistics.getTotalConnections(), 10);
	}
	
	@Test
	public void shouldGetActiveConnections() throws SQLException {
		when(dataSource.getNumBusyConnectionsAllUsers()).thenReturn(10);
		assertEquals(statistics.getActiveConnections(), 10);
	}
	
	@Test
	public void shouldGetIdleConnections() throws SQLException {
		when(dataSource.getNumIdleConnectionsAllUsers()).thenReturn(10);
		assertEquals(statistics.getIdleConnections(), 10);
	}
	
	@Test
	public void shouldGetAwaitingCheckout() throws SQLException {
		when(dataSource.getNumThreadsAwaitingCheckoutDefaultUser()).thenReturn(10);
		assertEquals(statistics.getAwaitingCheckout(), 10);
	}
	
	@Test
	public void shouldGetCachedStatements() throws SQLException {
		when(dataSource.getStatementCacheNumStatementsAllUsers()).thenReturn(10);
		assertEquals(statistics.getCachedStatements(), 10);
	}
	
	@Test
	public void shouldGetFailedCheckins() throws SQLException {
		when(dataSource.getNumFailedCheckinsDefaultUser()).thenReturn(10L);
		assertEquals(statistics.getFailedCheckins(), 10L);
	}
	
	@Test
	public void shouldGetFailedCheckouts() throws SQLException {
		when(dataSource.getNumFailedCheckoutsDefaultUser()).thenReturn(10L);
		assertEquals(statistics.getFailedCheckouts(), 10L);
	}
	
	@Test
	public void shouldGetFailedIdleTests() throws SQLException {
		when(dataSource.getNumFailedIdleTestsDefaultUser()).thenReturn(10L);
		assertEquals(statistics.getFailedIdleTests(), 10L);
	}
	
	@Test
	public void shouldGetStartTime() throws SQLException {
		when(dataSource.getStartTimeMillisDefaultUser()).thenReturn(10010101001L);
		assertEquals(statistics.getStartTime(), 10010101001L);
	}
	
	@Test
	public void shouldGetUpTime() throws SQLException {
		when(dataSource.getUpTimeMillisDefaultUser()).thenReturn(10010101001L);
		assertEquals(statistics.getUpTime(), 10010101001L);
	}
}
