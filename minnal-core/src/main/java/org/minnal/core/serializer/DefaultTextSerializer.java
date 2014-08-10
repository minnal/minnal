/**
 * 
 */
package org.minnal.core.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import java.util.Collection;

import org.minnal.core.MinnalException;

import com.google.common.base.Charsets;

/**
 * @author ganeshs
 *
 */
public class DefaultTextSerializer extends Serializer {
	
	public DefaultTextSerializer() {
	}
	
	public ByteBuf serialize(Object object) {
		ByteBuf buffer = Unpooled.buffer();
		ByteBufOutputStream os = new ByteBufOutputStream(buffer);
		try {
			os.write(object.toString().getBytes());
		} catch (Exception e) {
			throw new MinnalException("Failed while serializing the object", e);
		}
		return buffer;
	}

	public <T> T deserialize(ByteBuf buffer, Class<T> targetClass) {
		if (! targetClass.equals(String.class)) {
			throw new IllegalArgumentException("Target class is not string");
		}
		try {
			return (T) buffer.toString(Charsets.UTF_8);
		} catch (Exception e) {
			throw new MinnalException("Failed while deserializing the buffer to type - " + targetClass, e);
		}
	}
	
	@Override
	public <T extends Collection<E>, E> T deserializeCollection(ByteBuf buffer, Class<T> collectionType, Class<E> elementType) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
