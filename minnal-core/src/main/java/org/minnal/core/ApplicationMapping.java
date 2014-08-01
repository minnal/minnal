/**
 * 
 */
package org.minnal.core;

import static org.minnal.utils.http.HttpUtil.structureUrl;
import io.netty.handler.codec.http.HttpRequest;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.util.Comparators;
import org.minnal.utils.http.HttpUtil;

/**
 * Manages the mapping between mount path and application. Resolves the application mount paths to absolute path of the container.
 * <p/>
 * The request url will be mapped as the following,
 * <pre>
 * http://localhost:3000/&lt;base-path&gt;/&lt;application-path&gt;/&lt;resource-path&gt;
 * <p/>
 * 
 * @author ganeshs
 */
public class ApplicationMapping {

	private Map<String, Application<ApplicationConfiguration>> applications = new HashMap<String, Application<ApplicationConfiguration>>();
	
	private String basePath;
	
	/**
	 * Constructor with base path of the container. Expects a path starting with '/' character. Defaults to '/' if path is null or empty. 
	 * Structures the url to ensure it starts with '/' and ends without a '/'
	 * 
	 * @param basePath the base path of the container
	 */
	public ApplicationMapping(String basePath) {
		this.basePath = structureUrl(basePath);
	}
	
	/**
	 * Maps application to its mount path. If the mount path already exists or if the application is already mapped to another mount path, 
	 * throws an exception
	 * 
	 * @param application
	 * @param mountPath
	 */
	public void addApplication(Application<ApplicationConfiguration> application) {
		String mountPath = structureUrl(application.getConfiguration().getBasePath());
		String path = getAbsolutePath(mountPath);
		if (applications.containsKey(path)) {
			throw new IllegalArgumentException("Mount path - " + mountPath + " already exists. Either change the application base path or override it in container");
		}
		if (applications.containsValue(application)) {
			throw new IllegalArgumentException("Application - " + application + " is already mounted on a different mount path");
		}
		application.setPath(HttpUtil.createURI(path));
		applications.put(path, application);
	}
	
	/**
	 * Unmaps a mount path from the application. Throws an exception if the mount path doesn't exist
	 * 
	 * @param mountPath
	 * @return the application that's unmapped from the mount path
	 */
	public Application<ApplicationConfiguration> removeApplication(String mountPath) {
		mountPath = structureUrl(mountPath);
		String path = getAbsolutePath(mountPath);
		if (! applications.containsKey(path)) {
			throw new IllegalArgumentException("Mount path - " + mountPath+ " doesn't exist");
		}
		return applications.remove(path);
	}
	
	/**
	 * Resolves the request to an application by mapping the request url to the application path.
	 * 
	 * @param request
	 * @return the application that this request resolves to
	 */
	public Application<ApplicationConfiguration> resolve(HttpRequest request) {
		String path = request.getUri();
		for (Entry<String, Application<ApplicationConfiguration>> entry : getSortedApplications().entrySet()) {
			if (path.startsWith(entry.getKey())) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	/**
	 * Returns all the applications managed by the container
	 * 
	 * @return
	 */
	public Collection<Application<ApplicationConfiguration>> getApplications() {
		return applications.values();
	}
	
	/**
	 * @return the basePath
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * Returns the absolute path for the given mount path
	 * 
	 * @param mountPath
	 * @return
	 */
	private String getAbsolutePath(String mountPath) {
		return basePath + mountPath;
	}
	
	private Map<String, Application<ApplicationConfiguration>> getSortedApplications() {
		Map<String, Application<ApplicationConfiguration>> applications = 
				new TreeMap<String, Application<ApplicationConfiguration>>(Comparators.LENGTH_COMPARATOR);
		applications.putAll(this.applications);
		return applications;
	}
}
