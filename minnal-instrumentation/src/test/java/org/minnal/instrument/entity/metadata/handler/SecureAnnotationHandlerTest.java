/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.minnal.instrument.entity.Secure;
import org.minnal.instrument.entity.Secure.Method;
import org.minnal.instrument.entity.metadata.PermissionMetaData;
import org.minnal.instrument.entity.metadata.SecurableMetaData;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;

/**
 * @author ganeshs
 *
 */
public class SecureAnnotationHandlerTest {

	@Test
	public void shouldHandleSecureAnnotation() {
		SecurableMetaData metaData = mock(SecurableMetaData.class);
		SecureAnnotationHandler handler = new SecureAnnotationHandler();
		Secure secure = mock(Secure.class);
		when(secure.permissions()).thenReturn(new String[] {"permission1", "permission2"});
		when(secure.method()).thenReturn(Method.GET);
		handler.handle(metaData, secure);
		verify(metaData).addPermissionMetaData(new PermissionMetaData(Method.GET.getMethod(), Arrays.asList("permission1", "permission2")));
	}
}
