/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import javax.persistence.ManyToMany;

import org.minnal.instrument.entity.DummyModel;
import org.minnal.instrument.entity.metadata.CollectionMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ManyToManyAnnotationHandlerTest {
	
	private ManyToMany annotation;
	
	private ManyToManyAnnotationHandler handler;
	
	private EntityMetaData metaData;

	@BeforeMethod
	public void setup() {
		handler = new ManyToManyAnnotationHandler();
		annotation = mock(ManyToMany.class);
		metaData = mock(EntityMetaData.class);
		when(metaData.getEntityClass()).thenReturn((Class) DummyModel.class);
	}
	
	@Test
	public void shouldGetAnnotationType() {
		assertEquals(handler.getAnnotationType(), ManyToMany.class);
	}
	
	@Test
	public void shouldAddToMetadataCollectionWhenOnMethod() throws Exception {
		Method method = DummyModel.class.getDeclaredMethod("getChildren");
		handler.handle(metaData, annotation, method);
		CollectionMetaData data = new CollectionMetaData(DummyModel.class, "children", DummyModel.class, Set.class, true);
		verify(metaData).addCollection(data);
	}
	
	@Test
	public void shouldAddToMetadataCollectionWhenOnField() throws Exception {
		Field field = DummyModel.class.getDeclaredField("children");
		handler.handle(metaData, annotation, field);
		CollectionMetaData data = new CollectionMetaData(DummyModel.class, "children", DummyModel.class, Set.class, true);
		verify(metaData).addCollection(data);
	}
	
	@Test
	public void shouldSetRightCollectionTypeWhenOnMethod() throws Exception {
		Method method = DummyModel.class.getDeclaredMethod("getSiblings");
		handler.handle(metaData, annotation, method);
		CollectionMetaData data = new CollectionMetaData(DummyModel.class, "siblings", DummyModel.class, List.class, true);
		verify(metaData).addCollection(data);
	}
	
	@Test
	public void shouldSetRightCollectionTypeWhenOnField() throws Exception {
		Field field = DummyModel.class.getDeclaredField("siblings");
		handler.handle(metaData, annotation, field);
		CollectionMetaData data = new CollectionMetaData(DummyModel.class, "siblings", DummyModel.class, List.class, true);
		verify(metaData).addCollection(data);
	}
}
