/**
 * 
 */
package org.minnal.instrument.util;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.google.common.base.CaseFormat;

/**
 * @author ganeshs
 *
 */
public class DynaBean {
	
	private Map<String, Object> map = new HashMap<String, Object>();

	@JsonAnyGetter
	public Object get(String name) {
		return map.get(name);
	}
	
	@JsonAnySetter
	public void set(String name, Object value) {
		map.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name), value);
	}

	/**
	 * @return the map
	 */
	public Map<String, Object> getAttributes() {
		return map;
	}
}
