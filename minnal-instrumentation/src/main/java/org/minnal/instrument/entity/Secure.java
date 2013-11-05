/**
 * 
 */
package org.minnal.instrument.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jboss.netty.handler.codec.http.HttpMethod;

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
		
		private HttpMethod method;
		
		private Method(HttpMethod method) {
			this.method = method;
		}
		
		public HttpMethod getMethod() {
			return method;
		}
	}

	public Method method();
	
	public String[] permissions();
}
