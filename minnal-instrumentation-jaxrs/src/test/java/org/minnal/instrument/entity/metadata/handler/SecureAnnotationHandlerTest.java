/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.minnal.instrument.entity.Secure;
import org.minnal.instrument.entity.Secure.Method;
import org.minnal.instrument.entity.metadata.PermissionMetaData;
import org.minnal.instrument.entity.metadata.SecurableMetaData;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

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
		verify(metaData).addPermissionMetaData(new PermissionMetaData(Method.GET.getMethod(), Sets.newHashSet("permission1", "permission2")));
	}
}
