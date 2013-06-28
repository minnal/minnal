package org.minnal.instrument.util;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;

import org.minnal.instrument.entity.Searchable;
import org.testng.annotations.Test;


public class ClassUtilsTest {
	public ClassUtilsTest() {
	}

	class TempEntity extends  TempSuperEntity{
		
		@Searchable
		String field1;
		
		public String getField1() {
			return field1;
		}

		/**
		 * @param field8 the field8 to set
		 */
		public void setField1(String field1) {
			this.field1 = field1;
		}
		

	}
	
	class TempSuperEntity{
		
		@Searchable
		private String field8;

		/**
		 * @return the field8
		 */
		public String getField8() {
			return field8;
		}

		/**
		 * @param field8 the field8 to set
		 */
		public void setField8(String field8) {
			this.field8 = field8;
		}
		 
	}
	
	@Test
	public void shouldDiscoverSuperClassFields(){
		LinkedList<Field> allFields = ClassUtils.getAllFields(new LinkedList<Field>(), TempEntity.class);
		assertEquals(allFields.size(), 4);
	}
	
	@Test
	public void shouldDiscoverSuperClassMethods(){
		LinkedList<Method> allMethods = ClassUtils.getAllMethods(new LinkedList<Method>(), TempEntity.class);
		assertEquals(allMethods.size(), 16);
	}
}
