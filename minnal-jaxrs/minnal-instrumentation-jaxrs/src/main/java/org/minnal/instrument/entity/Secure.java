/**
 * 
 */
package org.minnal.instrument.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.HttpMethod;

/**
 * Annotation used to specify the permissions required to access an aggregate root, collections and action methods
 * 
 * @see SecureMultiple
 * @author ganeshs
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Secure {
	
	public enum Method {
		GET(HttpMethod.GET), POST(HttpMethod.POST), PUT(HttpMethod.PUT), DELETE(HttpMethod.DELETE);
		
		private String method;
		
		private Method(String method) {
			this.method = method;
		}
		
		public String getMethod() {
			return method;
		}
	}

	public Method method();
	
	public String[] permissions();
}
