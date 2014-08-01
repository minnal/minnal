/**
 * 
 */
package org.minnal.utils.http;

import static org.testng.Assert.assertEquals;

import java.net.URLEncoder;

import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class HttpUtilTest {

	@Test
	public void shouldDecodeUTF8EncodedString() throws Exception {
		String value = URLEncoder.encode("1234/1", "UTF-8");
		assertEquals(HttpUtil.decode(value), "1234/1");
	}
	
	@Test
	public void shouldDecodeNonUTF8EncodedString() throws Exception {
		String value = URLEncoder.encode("12341", "UTF-8");
		assertEquals(HttpUtil.decode(value), "12341");
	}
	
	@Test
	public void shouldStructureUrlIfEmptyOrNull() {
		assertEquals(HttpUtil.structureUrl(null), HttpUtil.SEPARATOR);
		assertEquals(HttpUtil.structureUrl(""), HttpUtil.SEPARATOR);
	}
}
