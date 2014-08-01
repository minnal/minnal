/**
 * 
 */
package org.minnal.security.auth.cas;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.activejpa.entity.testng.BaseModelTest;
import org.activejpa.jpa.JPA;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class JpaPgtStorageTest extends BaseModelTest {
	
	private JpaPgtStorage storage;
	
	@BeforeClass
	public void beforeClass() {
		JPA.instance.addPersistenceUnit("test");
	}
	
	public void setup() throws Exception {
		super.setup();
		storage = new JpaPgtStorage();
	}

	@Test
	public void shouldSavePgtIou() {
		storage.save("testiou", "testpgt");
		assertEquals(CasPgtIou.count(), 1);
	}
	
	@Test
	public void shouldNotSavePgtIouIfPgtIsNull() {
		storage.save("testiou", null);
		assertEquals(CasPgtIou.count(), 0);
	}
	
	@Test
	public void shouldNotSavePgtIouIfIouIsNull() {
		storage.save(null, "testpgt");
		assertEquals(CasPgtIou.count(), 0);
	}
	
	@Test
	public void shouldRetrivePgtFromIou() {
		storage.save("testiou", "testpgt");
		assertEquals(storage.retrieve("testiou"), "testpgt");
	}
	
	@Test
	public void shouldReturnNullIfIouNotFound() {
		storage.save("testiou", "testpgt");
		assertNull(storage.retrieve("testiou1"));
	}
	
	@Test
	public void shouldCleanUp() {
		storage.save("testiou", "testpgt");
		storage.save("testiou1", "testpgt1");
		storage.cleanUp();
		assertEquals(CasPgtIou.count(), 0);
	}
}
