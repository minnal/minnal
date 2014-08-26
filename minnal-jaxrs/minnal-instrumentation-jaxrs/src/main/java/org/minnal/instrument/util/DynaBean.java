/**
 * 
 */
package org.minnal.instrument.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.google.common.base.CaseFormat;

/**
 * @author ganeshs
 *
 */
public class DynaBean extends HashMap<String, Object> {
	
	public DynaBean() {
	}
	
	DynaBean(Map<String, Object> map) {
		for (Entry<String, Object> entry : map.entrySet()) {
			set(entry.getKey(), entry.getValue());
		}
	}

	@JsonAnyGetter
	public Object get(String name) {
		return super.get(name);
	}
	
	@Override
	public Object put(String name, Object value) {
		return set(name, value);
	}
	
	@SuppressWarnings("unchecked")
	@JsonAnySetter
	public Object set(String name, Object value) {
		if (value instanceof Map) {
			value = new DynaBean((Map<String, Object>) value);
		} else if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>) value;
			List<DynaBean> list = new ArrayList<DynaBean>();
			if (! collection.isEmpty() && collection.iterator().next() instanceof Map) {
				for (Object val : collection) {
					list.add(new DynaBean((Map<String, Object>)val));
				}
				value = list;
			}
		}
		return super.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name), value);
	}
}
