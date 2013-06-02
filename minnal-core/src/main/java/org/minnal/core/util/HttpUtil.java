/**
 * 
 */
package org.minnal.core.util;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

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
	
	public static URI createURI(String scheme, String host, String path) {
		String uri = scheme + "://" + host + path;
		return createURI(uri);
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

	public static Map<String, String> getCookies(String cookieHeader) {
		Map<String, String> cookies = new HashMap<String, String>();
		if(Strings.isNullOrEmpty(cookieHeader)) {
			return cookies;
		}
		
		StringTokenizer tokenizer = new StringTokenizer(cookieHeader, ";");
		String header = null;
		String value = null;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			int index = token.indexOf("=");
			if (index > 0) {
				header = token.substring(0, index).trim();
				value = token.substring(index + 1).trim();
				if (! cookies.containsKey(header)) {
					cookies.put(header, value);
				}
			}
		}
		return cookies;
	}
	
	public static String createCookie(Map<String, String> cookies) {
		StringBuffer buffer = new StringBuffer();
		for(Entry<String, String> entry : cookies.entrySet()) {
			buffer.append(entry.getKey() + "=" + entry.getValue());
//			buffer.append(";");
		}
		return buffer.toString();
	}
	
}
