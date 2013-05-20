/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.OneToOne;

import org.minnal.instrument.entity.DummyModel;
import org.minnal.instrument.entity.metadata.AssociationMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class OneToOneAnnotationHandlerTest {
	
	private OneToOne annotation;
	
	private OneToOneAnnotationHandler handler;
	
	private EntityMetaData metaData;

	@BeforeMethod
	public void setup() {
		handler = new OneToOneAnnotationHandler();
		annotation = mock(OneToOne.class);
		metaData = mock(EntityMetaData.class);
	}
	
	@Test
	public void shouldGetAnnotationType() {
		assertEquals(handler.getAnnotationType(), OneToOne.class);
	}
	
	@Test
	public void shouldAddToMetadataAssociationWhenOnMethod() throws Exception {
		Method method = DummyModel.class.getDeclaredMethod("getSpouse");
		handler.handle(metaData, annotation, method);
		AssociationMetaData data = new AssociationMetaData("spouse", DummyModel.class, true);
		verify(metaData).addAssociation(data);
	}
	
	@Test
	public void shouldAddToMetadataAssociationWhenOnField() throws Exception {
		Field field = DummyModel.class.getDeclaredField("spouse");
		handler.handle(metaData, annotation, field);
		AssociationMetaData data = new AssociationMetaData("spouse", DummyModel.class, true);
		verify(metaData).addAssociation(data);
	}
}
