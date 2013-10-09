/**
 * 
 */
package org.minnal.instrument.resource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.activejpa.entity.Filter;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.javalite.common.Inflector;
import org.minnal.core.MinnalException;
import org.minnal.core.Request;
import org.minnal.instrument.entity.metadata.ParameterMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

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
	
	private static final Logger logger = LoggerFactory.getLogger(ResourceUtil.class);

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
	
	/**
	 * Returns the request content as the given type. IF the content is a collection, returns a list of elements of the given type
	 * 
	 * @param request
	 * @param type
	 * @return
	 */
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
	
	/**
	 * Returns the paginated response
	 * 
	 * @param filter
	 * @param data
	 * @param total
	 * @return
	 */
	public static Map<String, Object> getPaginatedResponse(Filter filter, List data, long total) {
		java.util.Map<String, Object> map = new java.util.HashMap<String, Object>();
	    map.put(TOTAL, total);
	    map.put(COUNT, data.size());
	    map.put(PER_PAGE, filter.getPerPage());
	    map.put(PAGE_NO, filter.getPageNo());
	    map.put(DATA, data);
	    return map;
	}
	
	/**
	 * @param parameters
	 * @param request
	 * @param values
	 * @return
	 */
	private static Object[] getActionParameterValues(List<ParameterMetaData> parameters, Request request, Map<String, Object> values) {
		Map<String, Object> body = request.getContentAs(Map.class);
		if (body != null) {
			values.putAll(body);
		}
		Object[] params = new Object[parameters.size()];
		for (int i = 0; i < parameters.size(); i++) {
			ParameterMetaData param = parameters.get(i);
			Object value = values.get(param.getName());
			if (value != null && ! value.getClass().equals(param.getType())) {
				value = ConvertUtils.convert(value, param.getType());
			}
			params[i] = value;
		}
		return params;
	}
	
	/**
	 * TODO: Handle bulk actions
	 * 
	 * @param model
	 * @param actionName
	 * @param parameters
	 * @param request
	 * @param values
	 * @return
	 * @throws Throwable 
	 */
	public static Object invokeAction(Object model, String actionName, List<ParameterMetaData> parameters, Request request, Map<String, Object> values) throws Throwable {
		List<Class<?>> types = Lists.transform(parameters, new Function<ParameterMetaData, Class<?>>() {
			@Override
			public Class<?> apply(ParameterMetaData input) {
				return input.getType();
			}
		});
		Method method = MethodUtils.getAccessibleMethod(model.getClass(), actionName, types.toArray(new Class[0]));
		if (method == null) {
			logger.error("Unable to get the action - {} for the model - {}", actionName, model.getClass());
			throw new MinnalException("Unable to get the action - " + actionName + " for the model - " + model.getClass());
		}
		Object[] arguments = getActionParameterValues(parameters, request, values);
		try {
			return method.invoke(model, arguments);
		} catch (InvocationTargetException e) {
			logger.error("Failed while invoking the action - " + actionName + " for the model - " + model.getClass(), e);
			throw e.getCause();
		} catch (Exception e) {
			logger.error("Failed while invoking the action - " + actionName + " for the model - " + model.getClass(), e);
			throw new MinnalException("Failed while invoking the action - " + actionName + " for the model - " + model.getClass(), e);
		}
	}
}
