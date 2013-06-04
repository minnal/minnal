/**
 * 
 */
package org.minnal.core.serializer;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * @author ganeshs
 *
 */
public class DefaultFormSerializer extends DefaultTextSerializer {

	@Override
	public ChannelBuffer serialize(Object object) {
		if (! (object instanceof Map)) {
			return super.serialize(object);
		} else {
			String data = Joiner.on("&").withKeyValueSeparator("=").join((Map)object);
			return super.serialize(data);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(ChannelBuffer buffer, Class<T> targetClass) {
		if (! targetClass.isAssignableFrom(Map.class)) {
			throw new IllegalArgumentException("Target class is not a map");
		}
		String data = super.deserialize(buffer, String.class);
		return (T) Splitter.on("&").omitEmptyStrings().withKeyValueSeparator("=").split(data);
	}

	@Override
	public <T extends Collection<E>, E> T deserializeCollection(ChannelBuffer buffer, Class<T> collectionType, Class<E> elementType) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
