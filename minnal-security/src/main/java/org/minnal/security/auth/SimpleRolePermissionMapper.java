/**
 * 
 */
package org.minnal.security.auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.minnal.core.MinnalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class SimpleRolePermissionMapper implements RolePermissionMapper {
	
	private static final String DEFAULT_FILE = "role_permissions.properties";
	
	private String mappingFile = DEFAULT_FILE;
	
	private Map<Role, List<Permission>> permissions;
	
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
	public List<Permission> getPermissions(Role role) {
		if (permissions == null) {
			loadPermissions();
		}
		return permissions.get(role);
	}
	
	protected void loadPermissions() {
		Properties properties = new Properties();
		permissions = new HashMap<Role, List<Permission>>();
		
		try {
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(mappingFile));
			for (Entry<Object, Object> entry : properties.entrySet()) {
				Iterable<String> perms = Splitter.on(",").omitEmptyStrings().split((String) entry.getValue());
				permissions.put(new Role((String) entry.getKey()), Lists.newArrayList(Iterables.transform(perms, new Function<String, Permission>() {
					@Override
					public Permission apply(String input) {
						return new Permission(input);
					}
				})));
			}
		} catch (IOException e) {
			logger.error("Failed while loading the property file - " + mappingFile, e);
			throw new MinnalException("Failed while loading the property file - " + mappingFile, e);
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
