/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import javax.persistence.OneToMany;

import org.minnal.instrument.entity.DummyModel;
import org.minnal.instrument.entity.metadata.CollectionMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class OneToManyAnnotationHandlerTest {
	
	private OneToMany annotation;
	
	private OneToManyAnnotationHandler handler;
	
	private EntityMetaData metaData;

	@BeforeMethod
	public void setup() {
		handler = new OneToManyAnnotationHandler();
		annotation = mock(OneToMany.class);
		metaData = mock(EntityMetaData.class);
	}
	
	@Test
	public void shouldGetAnnotationType() {
		assertEquals(handler.getAnnotationType(), OneToMany.class);
	}
	
	@Test
	public void shouldAddToMetadataCollectionWhenOnMethod() throws Exception {
		Method method = DummyModel.class.getDeclaredMethod("getChildren");
		handler.handle(metaData, annotation, method);
		CollectionMetaData data = new CollectionMetaData("children", DummyModel.class, Set.class, true);
		verify(metaData).addCollection(data);
	}
	
	@Test
	public void shouldAddToMetadataCollectionWhenOnField() throws Exception {
		Field field = DummyModel.class.getDeclaredField("children");
		handler.handle(metaData, annotation, field);
		CollectionMetaData data = new CollectionMetaData("children", DummyModel.class, Set.class, true);
		verify(metaData).addCollection(data);
	}
	
	@Test
	public void shouldSetRightCollectionTypeWhenOnMethod() throws Exception {
		Method method = DummyModel.class.getDeclaredMethod("getSiblings");
		handler.handle(metaData, annotation, method);
		CollectionMetaData data = new CollectionMetaData("siblings", DummyModel.class, List.class, true);
		verify(metaData).addCollection(data);
	}
	
	@Test
	public void shouldSetRightCollectionTypeWhenOnField() throws Exception {
		Field field = DummyModel.class.getDeclaredField("siblings");
		handler.handle(metaData, annotation, field);
		CollectionMetaData data = new CollectionMetaData("siblings", DummyModel.class, List.class, true);
		verify(metaData).addCollection(data);
	}
}
