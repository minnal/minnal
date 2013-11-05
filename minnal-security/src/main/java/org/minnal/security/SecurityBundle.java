/**
 * 
 */
package org.minnal.security;

import org.minnal.core.Bundle;
import org.minnal.core.Container;
import org.minnal.security.auth.AuthorizationHandler;

/**
 * @author ganeshs
 *
 */
public class SecurityBundle implements Bundle<SecurityBundleConfiguration> {
	
	private AuthorizationHandler authorizationHandler = new AuthorizationHandler();

	@Override
	public void init(Container container, SecurityBundleConfiguration configuration) {
		container.registerListener(authorizationHandler);
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
