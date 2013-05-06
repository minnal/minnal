/**
 * 
 */
package org.minnal.core.serializer;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * @author ganeshs
 *
 */
public class DefaultJsonSerializer extends Serializer {

	public ChannelBuffer serialize(Object object) {
		return null;
	}

	public <T> T deserialize(ChannelBuffer buffer, Class<T> targetClass) {
		return null;
	}

}
