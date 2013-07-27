/**
 * 
 */
package org.minnal.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.ConfigurationProvider;
import org.minnal.core.config.ContainerConfiguration;
import org.minnal.core.util.Generics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class Container implements Lifecycle {

	private Router router;
	
	private ApplicationMapping applicationMapping;
	
	private ContainerConfiguration configuration;
	
	private List<ContainerLifecycleListener> listeners = new ArrayList<ContainerLifecycleListener>();
	
	private ContainerMessageObserver messageObserver = new ContainerMessageObserver();
	
	private List<Bundle<BundleConfiguration>> bundles = new ArrayList<Bundle<BundleConfiguration>>();
	
	private static ConfigurationProvider configurationProvider = ConfigurationProvider.getDefault();
	
	private static final Logger logger = LoggerFactory.getLogger(Container.class);
	
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
		router.registerListener(messageObserver);
	}
	
	public void init() {
		loadBundles();
		loadApplications();	
	}
	
	protected void loadApplications() {
		logger.info("Loading the applications");
		for (Application<ApplicationConfiguration> application : ServiceLoader.load(Application.class)) {
			String mountPath = configuration.getMounts().get(application.getClass().getName());
			if (mountPath == null) {
				mountPath = application.getConfiguration().getBasePath();
			} else {
				application.getConfiguration().setBasePath(mountPath);
			}
			mount(application);
		}
	}
	
	/**
	 * Loads all the bundles using SPI.
	 */
	protected void loadBundles() {
		logger.info("Loading the bundles from service loader");
		ServiceLoader<Bundle> loader = ServiceLoader.load(Bundle.class);
		List<Bundle> bundles = Lists.newArrayList(loader);
		Collections.sort(bundles, new Comparator<Bundle>() {
			@Override
			public int compare(Bundle o1, Bundle o2) {
				if (o1.getOrder() == o2.getOrder()) {
					return 0;
				}
				return o1.getOrder() < o2.getOrder() ? -1 : 1;
			}
		});
		for (Bundle<BundleConfiguration> bundle : bundles) {
			Class<BundleConfiguration> configClass = Generics.getTypeParameter(bundle.getClass(), BundleConfiguration.class);
			BundleConfiguration bundleConfig = configuration.getBundleOverrides().get(bundle.getClass().getCanonicalName());
			if (bundleConfig == null) {
				try {
					bundleConfig = (BundleConfiguration) configClass.newInstance();
				} catch (Exception e) {
					throw new MinnalException(e);
				}
			}
			bundle.init(this, bundleConfig);
			this.bundles.add(bundle);
		}
	}
	
	/**
	 * Mounts the application on the container
	 * 
	 * @param application
	 */
	protected void mount(Application<ApplicationConfiguration> application) {
		logger.info("Mounting the application {} on the mount path {}", application, application.getConfiguration().getBasePath());
		application.getConfiguration().setParent(configuration);
		for (ContainerLifecycleListener listener : listeners) {
			listener.preMount(application);
		}
		application.init();
		applicationMapping.addApplication(application);
		for (ContainerLifecycleListener listener : listeners) {
			listener.postMount(application);
		}
	}
	
	/**
	 * Unmounts an application from the given mount url
	 * 
	 * @param mountUrl
	 */
	protected void unMount(String mountUrl) {
		logger.info("Unmounting the mount path {}", mountUrl);
		Application<ApplicationConfiguration> application = applicationMapping.removeApplication(mountUrl);
		for (ContainerLifecycleListener listener : listeners) {
			listener.preUnMount(application);
		}
		application.stop();
		for (ContainerLifecycleListener listener : listeners) {
			listener.postUnMount(application);
		}
	}

	public void start() {
		logger.info("Starting the container");
		for (ContainerLifecycleListener listener : listeners) {
			listener.beforeStart(this);
		}
		for (Bundle<BundleConfiguration> bundle : bundles) {
			bundle.start();
		}
		for (Application<ApplicationConfiguration> application : applicationMapping.getApplications()) {
			application.start();
		}
		for (ContainerLifecycleListener listener : listeners) {
			listener.afterStart(this);
		}
	}

	public void stop() {
		logger.info("Stopping the container");
		for (ContainerLifecycleListener listener : listeners) {
			listener.beforeStop(this);
		}
		for (Bundle<BundleConfiguration> bundle : bundles) {
			bundle.stop();
		}
		// FIXME What will application do on stop?
		for (Application<ApplicationConfiguration> application : applicationMapping.getApplications()) {
			application.stop();
		}
		for (ContainerLifecycleListener listener : listeners) {
			listener.afterStop(this);
		}
	}

	public void registerListener(ContainerLifecycleListener listener) {
		logger.trace("Registering the life cycle listener {}", listener.getClass());
		listeners.add(listener);
	}
	
	public void registerListener(MessageListener listener) {
		messageObserver.registerListener(listener);
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
	
	public ContainerMessageObserver getMessageObserver() {
		return messageObserver;
	}
}
