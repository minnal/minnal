/**
 * 
 */
package org.minnal.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.minnal.core.config.ApplicationConfiguration;

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
	
	private static final String SEPARATOR = "/";
	
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
	 * Maps a mount path to an application. If the mount path already exists or if the application is already mapped to another mount path, 
	 * throws an exception
	 * 
	 * @param application
	 * @param mountPath
	 */
	public void addApplication(Application<ApplicationConfiguration> application, String mountPath) {
		mountPath = structureUrl(mountPath);
		String path = getAbsolutePath(mountPath);
		if (applications.containsKey(path)) {
			throw new IllegalArgumentException("Mount path - " + mountPath + " already exists");
		}
		if (applications.containsValue(application)) {
			throw new IllegalArgumentException("Application - " + application + " is already mounted on a different mount path");
		}
		application.setPath(path);
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
	public Application<ApplicationConfiguration> resolve(Request request) {
		String path = request.getPath();
		for (Entry<String, Application<ApplicationConfiguration>> entry : applications.entrySet()) {
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
	
	/**
	 * Structures the url to look like a valid path.
	 * 
	 * @param url
	 * @return
	 */
	private String structureUrl(String url) {
		if (url == null || url == "") {
			return SEPARATOR;
		}
		if (! url.startsWith(SEPARATOR)) {
			url = SEPARATOR + url;
		}
		if (url.endsWith(SEPARATOR)) {
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}
}
