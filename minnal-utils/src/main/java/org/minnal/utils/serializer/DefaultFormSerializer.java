/**
 * 
 */
package org.minnal.utils.serializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * @author ganeshs
 *
 */
public class DefaultFormSerializer extends DefaultTextSerializer {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(InputStream stream, Class<T> targetClass) {
		if (! targetClass.isAssignableFrom(Map.class)) {
			throw new IllegalArgumentException("Target class is not a map");
		}
		String data = super.deserialize(stream, String.class);
		return (T) Splitter.on("&").omitEmptyStrings().withKeyValueSeparator("=").split(data);
	}

	@Override
	public <T extends Collection<E>, E> T deserializeCollection(InputStream stream, Class<T> collectionType, Class<E> elementType) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public void serialize(Object object, OutputStream stream) {
		if (! (object instanceof Map)) {
			super.serialize(object, stream);
		} else {
			String data = Joiner.on("&").withKeyValueSeparator("=").join((Map)object);
			super.serialize(data, stream);
		}
	}

}
