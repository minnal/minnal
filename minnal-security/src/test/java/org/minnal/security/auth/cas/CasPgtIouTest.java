/**
 * 
 */
package org.minnal.security.auth.cas;

import static org.testng.Assert.assertEquals;

import org.activejpa.entity.testng.BaseModelTest;
import org.activejpa.jpa.JPA;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class CasPgtIouTest extends BaseModelTest {
	
	@BeforeClass
	public void beforeClass() {
		JPA.instance.addPersistenceUnit("test");
	}

	@Test
	public void shouldPersistCasPgtIou() {
		CasPgtIou pgtIou = new CasPgtIou("testpgt", "testiou");
		pgtIou.persist();
		assertEquals(CasPgtIou.one("iou", "testiou"), pgtIou);
	}
}
