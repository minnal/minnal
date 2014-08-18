/**
 * 
 */
package org.minnal.instrument.resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Providers;

import org.activejpa.entity.Condition.Operator;
import org.activejpa.entity.Filter;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.javalite.common.Inflector;
import org.minnal.instrument.MinnalInstrumentationException;
import org.minnal.instrument.entity.metadata.ParameterMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

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

	public static Filter getFilter(MultivaluedMap<String, String> queryParams) {
		Integer perPage = (Integer) ConvertUtils.convert(queryParams.getFirst(PER_PAGE), Integer.class);
		Integer pageNo = (Integer) ConvertUtils.convert(queryParams.getFirst(PAGE_NO), Integer.class);
		return new Filter(perPage, pageNo);
	}
	
	public static Filter getFilter(MultivaluedMap<String, String> queryParams, final List<String> paramNames) {
		Filter filter = getFilter(queryParams);
		for (Entry<String, List<String>> entry : queryParams.entrySet()) {
			if (! paramNames.contains(entry.getKey())) {
				continue;
			}
			if (entry.getValue().size() == 1) {
				filter.addCondition(Inflector.camelize(entry.getKey(), false), entry.getValue().get(0));
			} else {
				filter.addCondition(Inflector.camelize(entry.getKey(), false), Operator.in, entry.getValue());
			}
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
	 * @param inputStream
	 * @param providers
	 * @param httpHeaders
	 * @param type
	 * @param genericType
	 * @param annotations
	 * @return
	 */
	public static <T> T getContent(InputStream inputStream, Providers providers, HttpHeaders httpHeaders, Class<T> type, Type genericType, Annotation[] annotations) {
		MessageBodyReader<T> reader = providers.getMessageBodyReader(type, genericType, annotations, httpHeaders.getMediaType());
		try {
			return reader.readFrom(type, genericType, annotations, httpHeaders.getMediaType(), httpHeaders.getRequestHeaders(), inputStream);
		} catch (Exception e) {
			throw new MinnalInstrumentationException("Failed while getting the content from the request stream", e);
		}
	}
	
	static <V> TypeToken<List<V>> listType(Class<V> clazz) {
		return new TypeToken<List<V>>() {}.where(new TypeParameter<V>() {}, TypeToken.of(clazz));
	}
	
	/**
	 * Returns the request content as the given type. IF the content is a collection, returns a list of elements of the given type
	 * 
	 * @param request
	 * @param type
	 * @return
	 */
	public static Object getContent(byte[] raw, Providers providers, HttpHeaders httpHeaders, Class<?> type) {
		try {
			return getContent(new ByteArrayInputStream(raw), providers, httpHeaders, type, type, null);
		} catch (MinnalInstrumentationException e) {
			logger.trace("Failed while getting the content from the request stream", e);
			Throwable ex = e;
			while (ex.getCause() != null) {
				if (ex.getCause() instanceof IOException) {
					return getContent(new ByteArrayInputStream(raw), providers, httpHeaders, List.class, listType(type).getType(), null);
				}
				ex = ex.getCause();
			}
			throw e;
		}
	}
	
	/**
	 * @param parameters
	 * @param request
	 * @param values
	 * @return
	 */
	private static Object[] getActionParameterValues(List<ParameterMetaData> parameters, Map<String, Object> body, Map<String, Object> values) {
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
	 * @param rawContent
	 * @param providers
	 * @param httpHeaders
	 * @param values
	 * @return
	 * @throws Throwable 
	 */
	public static Object invokeAction(Object model, String actionName, List<ParameterMetaData> parameters, byte[] rawContent, Providers providers, HttpHeaders httpHeaders, 
			Map<String, Object> values) throws Throwable {
		List<Class<?>> types = Lists.transform(parameters, new Function<ParameterMetaData, Class<?>>() {
			@Override
			public Class<?> apply(ParameterMetaData input) {
				return input == null ? null : input.getType();
			}
		});
		Method method = MethodUtils.getAccessibleMethod(model.getClass(), actionName, types.toArray(new Class[0]));
		if (method == null) {
			logger.error("Unable to get the action - {} for the model - {}", actionName, model.getClass());
			throw new MinnalInstrumentationException("Unable to get the action - " + actionName + " for the model - " + model.getClass());
		}
		Map<String, Object> payload = getContent(new ByteArrayInputStream(rawContent), providers, httpHeaders, Map.class, Map.class, null);
		Object[] arguments = getActionParameterValues(parameters, payload, values);
		try {
			return method.invoke(model, arguments);
		} catch (InvocationTargetException e) {
			logger.error("Failed while invoking the action - " + actionName + " for the model - " + model.getClass(), e);
			throw e.getCause();
		} catch (Exception e) {
			logger.error("Failed while invoking the action - " + actionName + " for the model - " + model.getClass(), e);
			throw new MinnalInstrumentationException("Failed while invoking the action - " + actionName + " for the model - " + model.getClass(), e);
		}
	}
}
