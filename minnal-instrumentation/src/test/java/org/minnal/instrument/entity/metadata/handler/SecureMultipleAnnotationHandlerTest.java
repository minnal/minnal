/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.minnal.instrument.entity.Secure;
import org.minnal.instrument.entity.SecureMultiple;
import org.minnal.instrument.entity.Secure.Method;
import org.minnal.instrument.entity.metadata.PermissionMetaData;
import org.minnal.instrument.entity.metadata.SecurableMetaData;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class SecureMultipleAnnotationHandlerTest {

	@Test
	public void shouldHandleSecureAnnotation() {
		SecurableMetaData metaData = mock(SecurableMetaData.class);
		SecureMultipleAnnotationHandler handler = new SecureMultipleAnnotationHandler();
		SecureMultiple multiple = mock(SecureMultiple.class);
		Secure secure1 = mock(Secure.class);
		when(secure1.permissions()).thenReturn(new String[] {"permission1", "permission2"});
		when(secure1.method()).thenReturn(Method.GET);
		Secure secure2 = mock(Secure.class);
		when(secure2.permissions()).thenReturn(new String[] {"permission3"});
		when(secure2.method()).thenReturn(Method.POST);
		when(multiple.value()).thenReturn(new Secure[]{secure1, secure2});
		handler.handle(metaData, multiple);
		verify(metaData).addPermissionMetaData(new PermissionMetaData(Method.GET.getMethod(), Arrays.asList("permission1", "permission2")));
		verify(metaData).addPermissionMetaData(new PermissionMetaData(Method.POST.getMethod(), Arrays.asList("permission3")));
	}
}
