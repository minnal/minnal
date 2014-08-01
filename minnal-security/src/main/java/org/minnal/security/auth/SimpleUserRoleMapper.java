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

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class SimpleUserRoleMapper implements UserRoleMapper {
	
	private static final String DEFAULT_FILE = "user_roles.properties";
	
	private String mappingFile = DEFAULT_FILE;
	
	private Map<String, List<Role>> roles;
	
	private static final Logger logger = LoggerFactory.getLogger(SimpleRolePermissionMapper.class);
	
	/**
	 * @param mappingFile
	 */
	public SimpleUserRoleMapper(String mappingFile) {
		this.mappingFile = mappingFile;
	}
	
	public SimpleUserRoleMapper() {
	}

	@Override
	public List<Role> getRoles(User user) {
		if (roles == null) {
			loadRoles();
		}
		return roles.get(user.getName());
	}
	
	protected void loadRoles() {
		Properties properties = new Properties();
		roles = new HashMap<String, List<Role>>();
		
		InputStream is = null;
		try {
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(mappingFile);
			if (is == null) {
				return;
			}
			properties.load(is);
			for (Entry<Object, Object> entry : properties.entrySet()) {
				Iterable<String> rols = Splitter.on(",").omitEmptyStrings().split((String) entry.getValue());
				roles.put((String) entry.getKey(), Lists.newArrayList(Iterables.transform(rols, new Function<String, Role>() {
					@Override
					public Role apply(String input) {
						return new Role(input);
					}
				})));
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
