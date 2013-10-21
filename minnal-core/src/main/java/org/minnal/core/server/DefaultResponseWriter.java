/**
 * 
 */
package org.minnal.core.server;

import java.util.Set;

import org.minnal.core.serializer.Serializer;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.net.MediaType;

/**
 * @author anand.karthik
 *
 */
public class DefaultResponseWriter implements ResponseWriter {
	
	private static final String EXCLUDE = "exclude";
	private static final String INCLUDE = "include";
	private ServerResponse response;

	/**
	 * @param response
	 */
	public DefaultResponseWriter(ServerResponse response) {
		this.response = response;
	}
	
	private Set<String> getParamsSetFromHeader(String headerName){
		return Sets.newHashSet(Splitter.on(",").trimResults().omitEmptyStrings().split(Strings.nullToEmpty(response.getRequest().getHeader(headerName))));
	}

	@Override
	public void write(Object content) {
		Serializer serializer = null;
		MediaType type = response.getPrefferedContentType();
		serializer = (type == MediaType.PLAIN_TEXT_UTF_8) ? Serializer.DEFAULT_TEXT_SERIALIZER : response.getSerializer(type);
		response.setContentType(type);
		Set<String> includeSet = getParamsSetFromHeader(INCLUDE);
		Set<String> excludeSet = getParamsSetFromHeader(EXCLUDE);
		response.setContent(serializer.serialize(content, excludeSet, includeSet));
		System.out.println(response.toString());
	}
}
