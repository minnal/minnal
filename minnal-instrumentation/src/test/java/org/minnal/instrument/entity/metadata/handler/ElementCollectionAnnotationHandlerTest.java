/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.ElementCollection;

import org.minnal.instrument.entity.DummyModel;
import org.minnal.instrument.entity.DummyModel.Address;
import org.minnal.instrument.entity.metadata.AssociationMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ElementCollectionAnnotationHandlerTest {
	
	private ElementCollection annotation;
	
	private ElementCollectionAnnotationHandler handler;
	
	private EntityMetaData metaData;

	@BeforeMethod
	public void setup() {
		handler = new ElementCollectionAnnotationHandler();
		annotation = mock(ElementCollection.class);
		metaData = mock(EntityMetaData.class);
	}
	
	@Test
	public void shouldGetAnnotationType() {
		assertEquals(handler.getAnnotationType(), ElementCollection.class);
	}
	
	@Test
	public void shouldAddToMetadataAssociationWhenOnMethod() throws Exception {
		Method method = DummyModel.class.getDeclaredMethod("getAddresses");
		handler.handle(metaData, annotation, method);
		AssociationMetaData data = new AssociationMetaData("addresses", Address.class, false);
		verify(metaData).addAssociation(data);
	}
	
	@Test
	public void shouldAddToMetadataAssociationWhenOnField() throws Exception {
		Field field = DummyModel.class.getDeclaredField("addresses");
		handler.handle(metaData, annotation, field);
		AssociationMetaData data = new AssociationMetaData("addresses", Address.class, false);
		verify(metaData).addAssociation(data);
	}
}
