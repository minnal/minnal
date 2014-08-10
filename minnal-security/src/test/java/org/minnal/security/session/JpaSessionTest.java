/**
 * 
 */
package org.minnal.security.session;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.sql.Timestamp;

import org.activejpa.entity.testng.BaseModelTest;
import org.activejpa.jpa.JPA;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class JpaSessionTest extends BaseModelTest {
	
	@BeforeClass
	public void beforeClass() {
		JPA.instance.addPersistenceUnit("test");
	}

	@Test
	public void shouldCreateSession() {
		JpaSession session = new JpaSession("test123");
		session.persist();
		assertEquals(JpaSession.one("id", "test123"), session);
	}
	
	@Test
	public void shouldAddAttributesToSession() {
		JpaSession session = new JpaSession("test123");
		session.addAttribute("test", "testValue");
		session.persist();
		JpaSession newSession = JpaSession.one("id", "test123");
		assertEquals(newSession.getAttribute("test"), "testValue");
	}
	
	@Test
	public void shouldRemoveAttributesFromSession() {
		JpaSession session = new JpaSession("test123");
		session.addAttribute("test", "testValue");
		session.persist();
		session.removeAttribute("test");
		session.persist();
		JpaSession newSession = JpaSession.one("id", "test123");
		assertEquals(newSession.getAttribute("test"), null);
	}
	
	@Test
	public void shouldCheckIfAttributeExists() {
		JpaSession session = new JpaSession("test123");
		session.addAttribute("test", "testValue");
		session.persist();
		JpaSession newSession = JpaSession.one("id", "test123");
		assertTrue(newSession.containsAttribute("test"));
	}
	
	@Test
	public void shouldReturnSessionExpired() {
		JpaSession session = new JpaSession("test123");
		session.setCreatedAt(new Timestamp(System.currentTimeMillis() - 10000));
		assertTrue(session.hasExpired(5));
	}
	
	@Test
	public void shouldNotReturnSessionExpired() {
		JpaSession session = new JpaSession("test123");
		session.setCreatedAt(new Timestamp(System.currentTimeMillis() - 10000));
		assertFalse(session.hasExpired(100));
	}
}
