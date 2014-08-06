/**
 * 
 */
package org.minnal.security.auth;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.minnal.core.MinnalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class SimpleRolePermissionMapper implements RolePermissionMapper {
	
	private static final String DEFAULT_FILE = "role_permissions.properties";
	
	private String mappingFile = DEFAULT_FILE;
	
	private Map<String, List<String>> permissions;
	
	private static final Logger logger = LoggerFactory.getLogger(SimpleRolePermissionMapper.class);
	
	/**
	 * @param mappingFile
	 */
	public SimpleRolePermissionMapper(String mappingFile) {
		this.mappingFile = mappingFile;
	}
	
	public SimpleRolePermissionMapper() {
	}

	@Override
	public List<String> getPermissions(String role) {
		if (permissions == null) {
			loadPermissions();
		}
		return permissions.get(role);
	}
	
	protected void loadPermissions() {
		Properties properties = new Properties();
		permissions = new HashMap<String, List<String>>();
		
		InputStream is = null;
		try {
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(mappingFile);
			if (is == null) {
				return;
			}
			properties.load(is);
			for (Entry<Object, Object> entry : properties.entrySet()) {
				Iterable<String> perms = Splitter.on(",").omitEmptyStrings().split((String) entry.getValue());
				permissions.put((String) entry.getKey(), Lists.newArrayList(perms));
			}
		} catch (IOException e) {
			logger.error("Failed while loading the property file - " + mappingFile, e);
			throw new MinnalException("Failed while loading the property file - " + mappingFile, e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * @return the mappingFile
	 */
	public String getMappingFile() {
		return mappingFile;
	}

	/**
	 * @param mappingFile the mappingFile to set
	 */
	public void setMappingFile(String mappingFile) {
		this.mappingFile = mappingFile;
	}

}
