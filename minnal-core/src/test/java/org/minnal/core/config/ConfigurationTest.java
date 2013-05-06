/**
 * 
 */
package org.minnal.core.config;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.Arrays;
import java.util.HashSet;

import org.minnal.core.serializer.Serializer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
public class ConfigurationTest {
	
	private Configuration configuration;
	
	private Configuration parent;
	
	@BeforeMethod
	public void setup() {
		parent = new TestConfiguration();
		parent.setDefaultMediaType(MediaType.HTML_UTF_8);
		parent.addSerializer(MediaType.HTML_UTF_8, mock(Serializer.class));
		parent.addSerializer(MediaType.JSON_UTF_8, mock(Serializer.class));
		configuration = new TestConfiguration();
		configuration.setParent(parent);
	}
	
	@Test
	public void shouldGetSupportedMediaTypes() {
		configuration.addSerializer(MediaType.XML_UTF_8, mock(Serializer.class));
		configuration.addSerializer(MediaType.JSON_UTF_8, mock(Serializer.class));
		assertEquals(configuration.getSupportedMediaTypes(), new HashSet<MediaType>(Arrays.asList(MediaType.JSON_UTF_8, MediaType.XML_UTF_8)));
	}
	
	@Test
	public void shouldGetSupportedMediaTypesFromParent() {
		assertEquals(configuration.getSupportedMediaTypes(), new HashSet<MediaType>(Arrays.asList(MediaType.JSON_UTF_8, MediaType.HTML_UTF_8)));
	}
	
	@Test
	public void shouldNotReturnSerializerIfMediaTypeIsNotSupported() {
		configuration.addSerializer(MediaType.JSON_UTF_8, mock(Serializer.class));
		assertNull(configuration.getSerializer(MediaType.XML_UTF_8));
	}
	
	@Test
	public void shouldReturnSerializer() {
		configuration.addSerializer(MediaType.JSON_UTF_8, mock(Serializer.class));
		assertNotNull(configuration.getSerializer(MediaType.JSON_UTF_8));
		assertNotEquals(configuration.getSerializer(MediaType.JSON_UTF_8), parent.getSerializer(MediaType.JSON_UTF_8));
	}
	
	@Test
	public void shouldReturnSerializerFromParent() {
		assertNotNull(configuration.getSerializer(MediaType.JSON_UTF_8));
		assertEquals(configuration.getSerializer(MediaType.JSON_UTF_8), parent.getSerializer(MediaType.JSON_UTF_8));
	}
	
	@Test
	public void shouldReturnDefaultMediaTypes() {
		configuration.setParent(null);
		assertEquals(configuration.getSupportedMediaTypes(), new HashSet<MediaType>(Arrays.asList(MediaType.JSON_UTF_8, MediaType.XML_UTF_8)));
	}
	
	@Test
	public void shouldReturnDefaultSerializer() {
		configuration.setParent(null);
		assertEquals(configuration.getSerializer(MediaType.JSON_UTF_8), Serializer.DEFAULT_JSON_SERIALIZER);
		assertEquals(configuration.getSerializer(MediaType.XML_UTF_8), Serializer.DEFAULT_XML_SERIALIZER);
	}
	
	private class TestConfiguration extends Configuration {
	}
}
