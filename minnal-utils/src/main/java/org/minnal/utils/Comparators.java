/**
 * 
 */
package org.minnal.utils;

import java.util.Comparator;

import com.google.common.base.Preconditions;

/**
 * @author ganeshs
 *
 */
public class Comparators {

	public static final Comparator<String> LENGTH_COMPARATOR = new Comparator<String>() {
		public int compare(String o1, String o2) {
			Preconditions.checkNotNull(o1);
			Preconditions.checkNotNull(o2);
			return o1.length() == o2.length() ? 1 : o1.length() < o2.length() ? 1 : -1;
		}
	};
}
