/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.minnal.instrument.entity.DummyModel;
import org.minnal.instrument.entity.EntityKey;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class EntityKeyAnnotationHandlerTest {
	
	private EntityKeyAnnotationHandler handler;
	
	private EntityMetaData metaData;
	
	private EntityKey key;

	@BeforeMethod
	public void setup() {
		handler = new EntityKeyAnnotationHandler();
		metaData = mock(EntityMetaData.class);
		key = mock(EntityKey.class);
	}
	
	@Test
	public void shouldGetAnnotationType() {
		assertEquals(handler.getAnnotationType(), EntityKey.class);
	}
	
	@Test
	public void shouldSetEntityKeyOnMetaDataWhenOnMethod() throws Exception {
		Method method = DummyModel.class.getDeclaredMethod("getCode");
		handler.handle(metaData, key, method);
		verify(metaData).setEntityKey("code");
	}
	
	@Test
	public void shouldSetEntityKeyOnMetaDataWhenOnField() throws Exception {
		Field field = DummyModel.class.getDeclaredField("code");
		handler.handle(metaData, key, field);
		verify(metaData).setEntityKey("code");
	}
	
	@Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Method - readCode is not a getter")
	public void shouldThrowExceptionWhenMethodIsNotGetter() throws Exception {
		Method method = DummyModel.class.getDeclaredMethod("readCode");
		handler.handle(metaData, key, method);
	}
}
