/**
 * 
 */
package org.minnal.utils.scanner;

/**
 * A generic scanner interface. 
 * 
 * @author ganeshs
 *
 */
public interface Scanner<T> {

	void scan(Listener<T> listener);
	
	public interface Listener<T> {
		
		void handle(T t);
	}
}
