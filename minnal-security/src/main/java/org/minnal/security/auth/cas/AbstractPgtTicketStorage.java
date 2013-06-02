/**
 * 
 */
package org.minnal.security.auth.cas;

import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * @author ganeshs
 *
 */
@JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
public abstract class AbstractPgtTicketStorage implements ProxyGrantingTicketStorage {

	public void cleanUp() {
		// do nothing
	}

}
