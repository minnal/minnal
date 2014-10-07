/**
 * 
 */
package org.minnal.security.filter;

import org.minnal.security.session.Session;
import org.pac4j.core.profile.UserProfile;

/**
 * @author ganeshs
 *
 */
public interface AuthenticationListener {

    void authSuccess(Session session, UserProfile profile);
    
    void authFailed(Session session);
}
