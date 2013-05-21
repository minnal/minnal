/**
 * 
 */
package org.minnal.core.util;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.minnal.core.MinnalException;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;

/**
 * @author ganeshs
 *
 */
public class HttpUtil {

	public static Map<String, String> getQueryParameters(URI uri) {
		String query = uri.getQuery();
		Map<String, String> params = new HashMap<String, String>();
		if (! Strings.isNullOrEmpty(query)) {
			Iterator<String> iterator = Splitter.on("&").split(query).iterator();
			while(iterator.hasNext()) {
				String[] keyValue =  iterator.next().split("=");
				params.put(keyValue[0], keyValue[1]);
			}
		}
		return params;
	}
	
	public static URI createURI(String uri) {
		if (uri.endsWith("/") && uri.length() > 1) {
			uri = uri.substring(0, uri.length() - 1);
		}
		try {
			return new URI(uri);
		} catch (Exception e) {
			throw new MinnalException("Invalid uri - " + uri);
		}
	}
	
}
