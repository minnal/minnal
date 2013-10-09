/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import static org.testng.Assert.assertEquals;

import javax.persistence.OneToMany;

import org.minnal.instrument.entity.Action;
import org.minnal.instrument.entity.EntityKey;
import org.minnal.instrument.entity.Searchable;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class AbstractAnnotationHandlerTest {

	@Test
	public void shouldGetActionAnnotationHandler() throws Exception {
		final class c { @Action("action") public void actionMethod() {} }
		Action action = c.class.getDeclaredMethod("actionMethod").getAnnotation(Action.class);
		assertEquals(AbstractAnnotationHandler.handlerFor(action).getClass(), ActionAnnotationHandler.class);
	}
	
	@Test
	public void shouldGetEntityKeyAnnotationHandler() throws Exception {
		final class c { @EntityKey private String field; }
		EntityKey key = c.class.getDeclaredField("field").getAnnotation(EntityKey.class);
		assertEquals(AbstractAnnotationHandler.handlerFor(key).getClass(), EntityKeyAnnotationHandler.class);
	}
	
	@Test
	public void shouldGetSearchableAnnotationHandler() throws Exception {
		final class c { @Searchable private String field; }
		Searchable key = c.class.getDeclaredField("field").getAnnotation(Searchable.class);
		assertEquals(AbstractAnnotationHandler.handlerFor(key).getClass(), SearchableAnnotationHandler.class);
	}
	
	@Test
	public void shouldGetOneToManyAnnotationHandler() throws Exception {
		final class c { @OneToMany private String field; }
		OneToMany key = c.class.getDeclaredField("field").getAnnotation(OneToMany.class);
		assertEquals(AbstractAnnotationHandler.handlerFor(key).getClass(), OneToManyAnnotationHandler.class);
	}
}
