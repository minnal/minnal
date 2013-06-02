/**
 * 
 */
package org.minnal.security.session;

/**
 * @author ganeshs
 *
 */
public class JpaSessionStore implements SessionStore {

	public Session createSession(String id) {
		JpaSession session = new JpaSession(id);
		session.persist();
		return session;
	}

	public JpaSession getSession(String id) {
		return JpaSession.findById(id);
	}

	public void deleteSession(String id) {
		JpaSession.findById(id).delete();
	}

	public void save(Session session) {
		((JpaSession) session).persist();
	}
}
