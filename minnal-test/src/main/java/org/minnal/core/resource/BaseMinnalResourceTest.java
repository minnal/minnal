/**
 *
 */
package org.minnal.core.resource;

import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ResourceConfig;
import org.minnal.core.Application;
import org.minnal.core.Container;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.jaxrs.test.BaseJPAResourceTest;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.nio.ByteBuffer;

/**
 * @author ganeshs
 */
public abstract class BaseMinnalResourceTest extends BaseJPAResourceTest {

    private static Container container = new Container();

    @Override
    protected void init(ResourceConfig resourceConfig) {
        super.init(resourceConfig);
    }

    @BeforeSuite
    public void beforeSuite() {
        container.init();
        container.start();
    }

    @BeforeMethod
    public void beforeMethod() {
        Application<ApplicationConfiguration> application = container.getApplications().iterator().next();
        application.getObjectMapper().registerModule(new JodaModule());
        init(application.getResourceConfig());
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
    protected void setup() {
        super.setup();
    }

    @Override
    protected boolean disableForeignKeyChecks() {
        return super.disableForeignKeyChecks();
    }

    @Override
    protected ByteBuffer getByteBufferFromContainerResp(ContainerResponse response) {
        return super.getByteBufferFromContainerResp(response);
    }
}
