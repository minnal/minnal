/**
 * 
 */
package org.minnal.security.session;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import org.activejpa.entity.testng.BaseModelTest;
import org.activejpa.jpa.JPA;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class JpaSessionStoreTest extends BaseModelTest {
	
	private JpaSessionStore sessionStore;
	
	@BeforeClass
	public void beforeClass() {
		JPA.instance.addPersistenceUnit("test");
	}
	
	@BeforeMethod
	public void setup() throws Exception {
		super.setup();
		sessionStore = new JpaSessionStore();
	}
	
	@Test
	public void shouldCreateSession() {
		Session session = sessionStore.createSession("test123");
		assertNotNull(session);
		assertEquals(JpaSession.one("id", "test123"), session);
	}
	
	@Test
	public void shouldGetSession() {
		Session session = sessionStore.createSession("test123");
		assertEquals(sessionStore.getSession("test123"), session);
	}
	
	@Test
	public void shouldDeleteSession() {
		sessionStore.createSession("test123");
		sessionStore.deleteSession("test123");
		assertNull(sessionStore.getSession("test123"));
	}

	@Test
	public void shouldSaveSession() {
		Session session = sessionStore.createSession("test123");
		session.addAttribute("test123", "test123");
		sessionStore.save(session);
		session = sessionStore.getSession("test123");
		assertEquals(session.getAttribute("test123"), "test123");
	}
}
