/**
 * 
 */
package org.minnal.core.serializer;

import io.netty.buffer.ByteBuf;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * @author ganeshs
 *
 */
public class DefaultFormSerializer extends DefaultTextSerializer {

	@Override
	public ByteBuf serialize(Object object) {
		if (! (object instanceof Map)) {
			return super.serialize(object);
		} else {
			String data = Joiner.on("&").withKeyValueSeparator("=").join((Map)object);
			return super.serialize(data);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(ByteBuf buffer, Class<T> targetClass) {
		if (! targetClass.isAssignableFrom(Map.class)) {
			throw new IllegalArgumentException("Target class is not a map");
		}
		String data = super.deserialize(buffer, String.class);
		return (T) Splitter.on("&").omitEmptyStrings().withKeyValueSeparator("=").split(data);
	}

	@Override
	public <T extends Collection<E>, E> T deserializeCollection(ByteBuf buffer, Class<T> collectionType, Class<E> elementType) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
