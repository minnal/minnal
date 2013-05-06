/**
 * 
 */
package org.minnal.core.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.minnal.core.serializer.Serializer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.net.MediaType;

/**
 * Abstract class for all container, applications, resources and routes. The configuration can optionally inherit properties from its parent.
 * 
 * @author ganeshs
 *
 */
public abstract class Configuration {
	
	private Configuration parent;
	
	private String name;
	
	@JsonProperty
	private Map<MediaType, Serializer> serializers = new HashMap<MediaType, Serializer>();

	private MediaType defaultMediaType;
	
	private static final MediaType DEFAULT_MEDIA_TYPE = MediaType.JSON_UTF_8;
	
	private static final List<MediaType> SUPPORTED_TYPES = Arrays.asList(MediaType.JSON_UTF_8, MediaType.XML_UTF_8);
	
	public Configuration() {
	}
	
	public Configuration(String name) {
		this(name, null);
	}
	
	public Configuration(String name, Configuration parent) {
		this.name = name;
		this.parent = parent;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the parent
	 */
	public Configuration getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(Configuration parent) {
		this.parent = parent;
	}

	/**
	 * Returns the default media type. If not specified, looks at its ancestors for this value. If none of the ancestors has this value set,
	 * returns {@link #DEFAULT_MEDIA_TYPE} 
	 * 
	 * @return
	 */
	public MediaType getDefaultMediaType() {
		if (defaultMediaType == null) {
			if (parent != null) {
				return parent.getDefaultMediaType();
			}
			return DEFAULT_MEDIA_TYPE;
		}
		return defaultMediaType;
	}
	
	/**
	 * @param defaultMediaType the defaultMediaType to set
	 */
	public void setDefaultMediaType(MediaType defaultMediaType) {
		this.defaultMediaType = defaultMediaType;
	}

	/**
	 * Returns the supported media types. If not specified, looks at its ancestors for this value. If none of the ancestors has this value set,
	 * returns {@link #SUPPORTED_TYPES}
	 * 
	 * @return
	 */
	public Set<MediaType> getSupportedMediaTypes() {
		if (serializers.isEmpty()) {
			if (parent != null) {
				return parent.getSupportedMediaTypes();
			}
			return new HashSet<MediaType>(SUPPORTED_TYPES);
		}
		return serializers.keySet();
	}
	
	/**
	 * Adds a serializer for the given media type
	 * 
	 * @param mediaType
	 * @param serializer
	 */
	public void addSerializer(MediaType mediaType, Serializer serializer) {
		serializers.put(mediaType, serializer);
	}
	
	/**
	 * Checks if the given media type is supported
	 * 
	 * @param mediaType
	 * @return
	 */
	public boolean supportsMediaType(MediaType mediaType) {
		return getSupportedMediaTypes().contains(mediaType);
	}

	/**
	 * Returns the serializer for the given media type. If not specified, looks at its ancestors for this value. If none of the ancestors has this value set,
	 * returns the default serializers for json or xml media type
	 * 
	 * @param mediaType
	 * @return
	 */
	public Serializer getSerializer(MediaType mediaType) {
		if (! supportsMediaType(mediaType)) {
			return null;
		}
		if (serializers.isEmpty()) {
			if (parent != null) {
				return parent.getSerializer(mediaType);
			}
			if (mediaType.equals(MediaType.JSON_UTF_8)) {
				return Serializer.DEFAULT_JSON_SERIALIZER;
			} else if (mediaType.equals(MediaType.XML_UTF_8)) {
				return Serializer.DEFAULT_XML_SERIALIZER;
			}
		}
		return serializers.get(mediaType);
	}
}
