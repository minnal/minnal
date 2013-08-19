/**
 * 
 */
package org.minnal.instrument.util;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.minnal.instrument.entity.Searchable;
import org.minnal.utils.reflection.ClassUtils;
import org.testng.annotations.Test;

/**
 * @author anand.karthik
 *
 */
public class ClassUtilsTest {

	class TempEntity extends TempSuperEntity {

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

	class TempSuperEntity {

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
	public void shouldDiscoverSuperClassFields() {
		List<Field> allFields = ClassUtils.getAllFields(TempEntity.class);
		assertEquals(allFields.size(), 4);
	}

	@Test
	public void shouldDiscoverSuperClassMethods() {
		List<Method> allMethods = ClassUtils.getAllMethods(TempEntity.class);
		assertEquals(allMethods.size(), 16);
	}
}
