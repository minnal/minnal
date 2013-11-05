/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import java.lang.annotation.Annotation;

import org.minnal.instrument.entity.Secure;
import org.minnal.instrument.entity.SecureMultiple;
import org.minnal.instrument.entity.metadata.SecurableMetaData;

/**
 * @author ganeshs
 *
 */
public class SecureMultipleAnnotationHandler extends SecureAnnotationHandler {

	public void handle(SecurableMetaData metaData, Annotation annotation) {
		for (Secure secure : ((SecureMultiple) annotation).value()) {
			super.handle(metaData, secure);
		}
	}
}
