/**
 * 
 */
package org.minnal.instrument.resource;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.activejpa.entity.Filter;
import org.apache.commons.beanutils.ConvertUtils;
import org.javalite.common.Inflector;
import org.minnal.core.MinnalException;
import org.minnal.core.Request;

import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author ganeshs
 *
 */
public class ResourceUtil {
	
	public static final String PER_PAGE = "per_page";
	
	public static final String PAGE_NO = "page";
	
	public static final String TOTAL = "total";
	
	public static final String COUNT = "count";
	
	public static final String DATA = "data";

	public static Filter getFilter(Request request) {
		Integer perPage = (Integer) ConvertUtils.convert(request.getHeader(PER_PAGE), Integer.class);
		Integer pageNo = (Integer) ConvertUtils.convert(request.getHeader(PAGE_NO), Integer.class);
		return new Filter(perPage, pageNo);
	}
	
	public static Filter getFilter(Request request, List<String> paramNames) {
		Filter filter = getFilter(request);
		Map<String, String> params = request.getHeaders(paramNames);
		for (Entry<String, String> entry : params.entrySet()) {
			filter.addCondition(Inflector.camelize(entry.getKey(), false), entry.getValue());
		}
		return filter;
	}
	
	public static boolean isCommaSeparated(String value) {
		return value.contains(",");
	}
	
	public static String[] getCommaSeparatedValues(String value) {
		return value.split(",");
	}
	
	public static Object getContent(Request request, Class<?> type) {
		try {
			return request.getContentAs(type);
		} catch (MinnalException e) {
			Throwable ex = e;
			while (ex.getCause() != null && ex instanceof MinnalException) {
				if (ex.getCause() instanceof JsonMappingException) {
					request.getContent().resetReaderIndex();
					return request.getContentAs(List.class, type);
				}
				ex = ex.getCause();
			}
			throw e;
		}
	}
	
	public static Map<String, Object> getPaginatedResponse(Filter filter, List data, long total) {
		java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
	    map.put(TOTAL, total);
	    map.put(COUNT, data.size());
	    map.put(PER_PAGE, filter.getPerPage());
	    map.put(PAGE_NO, filter.getPageNo());
	    map.put("data", data);
	    return map;
	}
}
