/**
 * 
 */
package org.minnal.api;

import java.lang.annotation.Annotation;
import java.util.List;

import org.minnal.core.BundleConfiguration;

import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class ApiBundleConfiguration extends BundleConfiguration {

	private boolean enableCors = true;
	
	private List<Class<? extends Annotation>> excludedAnnotations = Lists.newArrayList();
	
	/**
	 * Default constructor
	 */
	public ApiBundleConfiguration() {
	}
	
	/**
	 * @param enableCors
	 * @param excludedAnnotations
	 */
	public ApiBundleConfiguration(boolean enableCors,
			List<Class<? extends Annotation>> excludedAnnotations) {
		this.enableCors = enableCors;
		this.excludedAnnotations = excludedAnnotations;
	}

	/**
	 * @return the enableCors
	 */
	public boolean isEnableCors() {
		return enableCors;
	}

	/**
	 * @param enableCors the enableCors to set
	 */
	public void setEnableCors(boolean enableCors) {
		this.enableCors = enableCors;
	}

	/**
	 * @return the excludedAnnotations
	 */
	public List<Class<? extends Annotation>> getExcludedAnnotations() {
		return excludedAnnotations;
	}

	/**
	 * @param excludedAnnotations the excludedAnnotations to set
	 */
	public void setExcludedAnnotations(
			List<Class<? extends Annotation>> excludedAnnotations) {
		this.excludedAnnotations = excludedAnnotations;
	}
}
