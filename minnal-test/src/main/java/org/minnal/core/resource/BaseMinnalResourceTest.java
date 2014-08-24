/**
 * 
 */
package org.minnal.core.resource;

import org.glassfish.jersey.server.ResourceConfig;
import org.minnal.core.Application;
import org.minnal.core.Container;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.jaxrs.test.provider.JacksonProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

/**
 * @author ganeshs
 *
 */
public abstract class BaseMinnalResourceTest extends org.minnal.jaxrs.test.BaseJPAResourceTest {

    @Override
    public void init(ResourceConfig resourceConfig, JacksonProvider provider) {
        super.init(resourceConfig, provider);
    }

    private static Container container = new Container();

    @BeforeSuite
    public void beforeSuite() {
        container.init();
        container.start();
        Application<ApplicationConfiguration> application = container.getApplications().iterator().next();
        init(application.getResourceConfig(), new JacksonProvider(application.getObjectMapper()));
    }

    @BeforeMethod
    public void beforeMethod() {
        setup();
    }

    @AfterMethod
    public void afterMethod() {
        destroy();
    }

    @AfterSuite
    public void afterSuite() {
        container.stop();
    }


    @Override
    public void setup() {
		super.setup();
	}
	
	/**
	 * Override this method if you don't want to disable foreign key checks 
	 * 
	 * @return
	 */
	protected boolean disableForeignKeyChecks() {
		return super.disableForeignKeyChecks();
	}

}
