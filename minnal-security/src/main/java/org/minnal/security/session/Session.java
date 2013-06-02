/**
 * 
 */
package org.minnal.security.session;

import java.io.Serializable;
import java.sql.Timestamp;


/**
 * @author ganeshs
 *
 */
public interface Session extends Serializable {
	
	String getId();
	
	<T> T getAttribute(String name);

	boolean containsAttribute(String name);
	
	void removeAttribute(String name);
	
	void addAttribute(String name, Object value);
	
	Timestamp getCreatedAt();
	
	boolean hasExpired(long timeoutInSecs);
}
