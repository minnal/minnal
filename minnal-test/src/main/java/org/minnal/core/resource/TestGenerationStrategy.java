/**
 * 
 */
package org.minnal.core.resource;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.GeneratedValue;

import org.minnal.autopojo.GenerationStrategy;
import org.minnal.autopojo.resolver.AttributeResolver;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author ganeshs
 *
 */
public class TestGenerationStrategy extends GenerationStrategy {

	@Override
	protected AttributeResolver getObjectResolver() {
		List<Class<? extends Annotation>> list = new ArrayList<Class<? extends Annotation>>();
		list.add(JsonBackReference.class);
		list.add(GeneratedValue.class);
		list.add(JsonIgnore.class);
		return new BiDirectionalObjectResolver(this, list, null);
	}
}
