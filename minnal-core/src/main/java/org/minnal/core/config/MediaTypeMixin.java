/**
 * 
 */
package org.minnal.core.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
public abstract class MediaTypeMixin {

	@JsonCreator
	public static MediaType parse(String input) {
		return null;
	}
}
