/**
 * 
 */
package org.minnal.core.serializer;

import java.util.Collection;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * @author ganeshs
 *
 */
public class DefaultXmlSerializer extends Serializer {

	public ChannelBuffer serialize(Object object) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	public <T> T deserialize(ChannelBuffer buffer, Class<T> targetClass) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public <T extends Collection<E>, E> T deserializeCollection(ChannelBuffer buffer, Class<T> collectionType, Class<E> elementType) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
