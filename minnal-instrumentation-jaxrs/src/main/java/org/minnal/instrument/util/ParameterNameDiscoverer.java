/**
 * 
 */
package org.minnal.instrument.util;

import java.lang.reflect.AccessibleObject;

import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.AnnotationParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

/**
 * @author ganeshs
 *
 */
public final class ParameterNameDiscoverer {

	private static Paranamer paranamer = new CachingParanamer(new AdaptiveParanamer(new AnnotationParanamer(), new BytecodeReadingParanamer()));
	
	/**
	 * Note: Will work only if the code is compiled in debug mode.
	 * 
	 * @param object
	 * @return
	 */
	public static String[] getParameterNames(AccessibleObject object) {
		return paranamer.lookupParameterNames(object, false);
	}
}
