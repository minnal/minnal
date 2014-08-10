/**
 * 
 */
package org.minnal.jpa.entity;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.activejpa.entity.Model;
import org.minnal.utils.scanner.Scanner.Listener;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class EntityScannerTest {

	@Test
	public void shouldScanEntitiesFromASinglePackage() {
		EntityScanner scanner = new EntityScanner("org.minnal.jpa.entity");
		Listener<Class<?>> listener = mock(Listener.class);
		scanner.scan(listener);
		verify(listener).handle(Model1.class);
		verify(listener).handle(Model2.class);
	}
	
	@Test
	public void shouldScanEntitiesFromMultiplePackage() {
		EntityScanner scanner = new EntityScanner("org.minnal.jpa.entity", "org.minnal.instrument.entity");
		Listener<Class<?>> listener = mock(Listener.class);
		scanner.scan(listener);
		verify(listener, times(2)).handle(any(Class.class));
		verify(listener).handle(Model1.class);
		verify(listener).handle(Model2.class);
	}
	
	@Test
	public void shouldScanEntitiesRecursivelyInAPackage() {
		EntityScanner scanner = new EntityScanner("org.minnal.jpa");
		Listener<Class<?>> listener = mock(Listener.class);
		scanner.scan(listener);
		verify(listener, times(2)).handle(any(Class.class));
		verify(listener).handle(Model1.class);
		verify(listener).handle(Model2.class);
	}
}

@Entity
class Model1 extends Model {
	@Id
	public Serializable getId() {
		return null;
	}
}

@Entity
class Model2 extends Model {
	@Id
	public Serializable getId() {
		return null;
	}
}