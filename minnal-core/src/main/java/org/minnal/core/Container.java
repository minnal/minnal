/**
 * 
 */
package org.minnal.core;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.ConfigurationProvider;
import org.minnal.core.config.ContainerConfiguration;
import org.minnal.core.config.DefaultConfigurationProvider;

/**
 * @author ganeshs
 *
 */
public class Container implements Lifecycle {

	private Router router;
	
	private ApplicationMapping applicationMapping;
	
	private ContainerConfiguration configuration;
	
	private List<ContainerLifecycleListener> listeners = new ArrayList<ContainerLifecycleListener>();
	
	private List<Bundle> bundles = new ArrayList<Bundle>();
	
	private static ConfigurationProvider configurationProvider = ConfigurationProvider.getDefault();
	
	public Container(String configPath) {
		this(configurationProvider.provide(ContainerConfiguration.class, configPath));
	}
	
	public Container() {
		this(configurationProvider.provide(ContainerConfiguration.class));
	}
	
	public Container(ContainerConfiguration configuration) {
		this.configuration = configuration;
		this.applicationMapping = new ApplicationMapping(configuration.getBasePath());
		router = new Router(applicationMapping);
		loadBundles();
		loadApplications();
	}
	
	protected void loadApplications() {
		for (Application<ApplicationConfiguration> application : ServiceLoader.load(Application.class)) {
			String mountPath = configuration.getMounts().get(application.getClass().getName());
			if (mountPath == null) {
				throw new MinnalException("Mount Path not specified for application - " + application.getClass().getName());
			}
			mount(application, mountPath);
		}
	}
	
	/**
	 * Loads all the bundles using SPI.
	 */
	protected void loadBundles() {
		ServiceLoader<Bundle> loader = ServiceLoader.load(Bundle.class);
		for (Bundle bundle : loader) {
			bundle.init(this);
			bundles.add(bundle);
		}
	}
	
	/**
	 * Mounts an application to a mount url
	 * 
	 * @param application
	 * @param mountUrl
	 */
	protected void mount(Application<ApplicationConfiguration> application, String mountUrl) {
		application.getConfiguration().setParent(configuration);
		application.init();
		applicationMapping.addApplication(application, mountUrl);
		for (ContainerLifecycleListener listener : listeners) {
			listener.onMount(application, mountUrl);
		}
	}
	
	/**
	 * Unmounts an application from the given mount url
	 * 
	 * @param mountUrl
	 */
	protected void unMount(String mountUrl) {
		Application<ApplicationConfiguration> application = router.getApplicationMapping().removeApplication(mountUrl);
		application.stop();
		for (ContainerLifecycleListener listener : listeners) {
			listener.onUnMount(application, mountUrl);
		}
	}

	public void start() {
		for (Application<ApplicationConfiguration> application : applicationMapping.getApplications()) {
			application.start();
		}
		
		for (ContainerLifecycleListener listener : listeners) {
			listener.beforeStart(this);
		}
		for (Bundle bundle : bundles) {
			bundle.start();
		}
		for (ContainerLifecycleListener listener : listeners) {
			listener.afterStart(this);
		}
	}

	public void stop() {
		for (ContainerLifecycleListener listener : listeners) {
			listener.beforeStop(this);
		}
		for (Bundle bundle : bundles) {
			bundle.stop();
		}
		for (ContainerLifecycleListener listener : listeners) {
			listener.afterStop(this);
		}
		// FIXME What will application do on stop?
		for (Application<ApplicationConfiguration> application : applicationMapping.getApplications()) {
			application.stop();
		}
	}

	public void registerListener(ContainerLifecycleListener listener) {
		listeners.add(listener);
	}

	/**
	 * @return the configuration
	 */
	public ContainerConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @return the router
	 */
	public Router getRouter() {
		return router;
	}
}
