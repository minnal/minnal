/**
 * 
 */
package org.minnal.security.session;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;


/**
 * @author ganeshs
 *
 */
@JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
public interface SessionStore {

	Session createSession(String id);
	
	Session getSession(String id);
	
	void deleteSession(String id);
	
	void save(Session session);
}
