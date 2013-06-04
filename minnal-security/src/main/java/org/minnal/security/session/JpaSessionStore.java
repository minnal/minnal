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
		JpaSession session = JpaSession.findById(id);
		if (session != null) {
			session.delete();
		}
	}

	public void save(Session session) {
		((JpaSession) session).persist();
	}
	
	public JpaSession findSessionBy(String key, String value) {
		if (key.equals("serviceTicket")) {
			return JpaSession.first(key, value);
		}
		return null;
	}
}
