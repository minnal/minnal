/**
 * 
 */
package org.minnal.utils.serializer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collection;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

/**
 * @author ganeshs
 *
 */
public class DefaultTextSerializer extends Serializer {
	
	public DefaultTextSerializer() {
	}
	
	public <T> T deserialize(InputStream stream, Class<T> targetClass) {
		if (! targetClass.equals(String.class)) {
			throw new IllegalArgumentException("Target class is not string");
		}
		try {
			InputStreamReader reader = new InputStreamReader(stream, Charsets.UTF_8);
			return (T) CharStreams.toString(reader);
		} catch (Exception e) {
			throw new SerializationException("Failed while deserializing the buffer to type - " + targetClass, e);
		}
	}
	
	@Override
	public <T extends Collection<E>, E> T deserializeCollection(InputStream buffer, Class<T> collectionType, Class<E> elementType) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void serialize(Object object, OutputStream stream) {
		try {
			stream.write(object.toString().getBytes(Charsets.UTF_8));
		} catch (Exception e) {
			throw new SerializationException("Failed while serializing the object", e);
		} finally {
			closeStream(stream);
		}
	}
}
