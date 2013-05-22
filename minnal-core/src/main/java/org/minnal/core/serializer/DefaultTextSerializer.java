/**
 * 
 */
package org.minnal.core.serializer;

import java.util.Collection;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.minnal.core.MinnalException;

import com.google.common.base.Charsets;

/**
 * @author ganeshs
 *
 */
public class DefaultTextSerializer extends Serializer {
	
	public DefaultTextSerializer() {
	}
	
	public ChannelBuffer serialize(Object object) {
		ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
		ChannelBufferOutputStream os = new ChannelBufferOutputStream(buffer);
		try {
			os.write(object.toString().getBytes());
		} catch (Exception e) {
			throw new MinnalException("Failed while serializing the object", e);
		}
		return buffer;
	}

	public <T> T deserialize(ChannelBuffer buffer, Class<T> targetClass) {
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
	public <T extends Collection<E>, E> T deserializeCollection(ChannelBuffer buffer, Class<T> collectionType, Class<E> elementType) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
