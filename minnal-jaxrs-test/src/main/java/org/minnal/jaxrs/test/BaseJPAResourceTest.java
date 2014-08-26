/**
 * 
 */
package org.minnal.jaxrs.test;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.activejpa.enhancer.ActiveJpaAgentLoader;
import org.activejpa.jpa.JPA;
import org.activejpa.jpa.JPAContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IObjectFactory;
import org.testng.ITestContext;
import org.testng.annotations.ObjectFactory;
import org.testng.internal.ObjectFactoryImpl;

/**
 * @author ganeshs
 *
 */
public abstract class BaseJPAResourceTest extends BaseResourceTest {
	
	protected static String[] DISABLE_REFERENTIAL_INTEGRITY_SQL = new String[] {
		"SET REFERENTIAL_INTEGRITY FALSE", // H2 Database 
		"SET DATABASE REFERENTIAL INTEGRITY FALSE", // HsqlDb 
		"SET FOREIGN_KEY_CHECKS = 0" // Mysql
	};
	
	private static final Logger logger = LoggerFactory.getLogger(BaseJPAResourceTest.class);
	
	
	/**
	 * Note: Kind of hack to ensure that ActiveJPAAgent instruments all the models before they are loaded.
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	@ObjectFactory
	public IObjectFactory getObjectFactory(ITestContext context) throws Exception {
		ActiveJpaAgentLoader.instance().loadAgent();
		return new ObjectFactoryImpl();
	}

	@Override
	protected void setup() {
		super.setup();
		if (! disableForeignKeyChecks()) {
			return;
		}
		
		JPAContext context = JPA.instance.getDefaultConfig().getContext();
		context.beginTxn();
		try {
			EntityManager manager = context.getEntityManager();
			for (String sql : DISABLE_REFERENTIAL_INTEGRITY_SQL) {
				try {
					Query query = manager.createNativeQuery(sql);
					query.executeUpdate();
					break;
				} catch (Exception e) {
					logger.debug("Failed while disabling the referential integrity", e);
				}
			}
		} finally {
			context.closeTxn(false);
		}
	}
	
	/**
	 * Override this method if you don't want to disable foreign key checks 
	 * 
	 * @return
	 */
	protected boolean disableForeignKeyChecks() {
		return true;
	}

}
