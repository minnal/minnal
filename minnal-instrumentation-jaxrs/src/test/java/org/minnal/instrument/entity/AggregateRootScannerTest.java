/**
 * 
 */
package org.minnal.instrument.entity;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.activejpa.entity.Model;
import org.minnal.core.scanner.Scanner.Listener;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class AggregateRootScannerTest {
	
	@Test
	public void shouldScanAggregateRootsFromASinglePackage() {
		AggregateRootScanner scanner = new AggregateRootScanner("org.minnal.instrument.entity");
		Listener<Class<?>> listener = mock(Listener.class);
		scanner.scan(listener);
		verify(listener, atLeast(2)).handle(any(Class.class));
		verify(listener).handle(Model1.class);
		verify(listener).handle(Model2.class);
	}
	
	@Test
	public void shouldScanAggregateRootsFromMultiplePackage() {
		AggregateRootScanner scanner = new AggregateRootScanner("org.minnal.instrument.resource", "org.minnal.instrument.entity");
		Listener<Class<?>> listener = mock(Listener.class);
		scanner.scan(listener);
		verify(listener, atLeast(2)).handle(any(Class.class));
		verify(listener).handle(Model1.class);
		verify(listener).handle(Model2.class);
	}
	
	@Test
	public void shouldScanAggregateRootsRecursivelyInAPackage() {
		AggregateRootScanner scanner = new AggregateRootScanner("org.minnal.instrument");
		Listener<Class<?>> listener = mock(Listener.class);
		scanner.scan(listener);
		verify(listener, atLeast(2)).handle(any(Class.class));
		verify(listener).handle(Model1.class);
		verify(listener).handle(Model2.class);
	}
}

@AggregateRoot
@Entity
class Model1 extends Model {
	@Id
	public Serializable getId() {
		return null;
	}
}

@AggregateRoot
@Entity
class Model2 extends Model {
	@Id
	public Serializable getId() {
		return null;
	}
}
