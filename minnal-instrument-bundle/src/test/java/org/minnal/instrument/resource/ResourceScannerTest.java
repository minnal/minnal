/**
 * 
 */
package org.minnal.instrument.resource;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.activejpa.entity.Model;
import org.minnal.core.resource.Resource;
import org.minnal.core.scanner.Scanner.Listener;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ResourceScannerTest {
	
	@Test
	public void shouldScanResourcesFromASinglePackage() {
		ResourceScanner scanner = new ResourceScanner("org.minnal.instrument.resource");
		Listener<Class<?>> listener = mock(Listener.class);
		scanner.scan(listener);
		verify(listener).handle(Resource1.class);
		verify(listener).handle(Resource2.class);
	}
	
	@Test
	public void shouldScanAggregateRootsFromMultiplePackage() {
		ResourceScanner scanner = new ResourceScanner("org.minnal.instrument.resource", "org.minnal.instrument.entity");
		Listener<Class<?>> listener = mock(Listener.class);
		scanner.scan(listener);
		verify(listener, times(2)).handle(any(Class.class));
		verify(listener).handle(Resource1.class);
		verify(listener).handle(Resource2.class);
	}
	
	@Test
	public void shouldScanAggregateRootsRecursivelyInAPackage() {
		ResourceScanner scanner = new ResourceScanner("org.minnal.instrument");
		Listener<Class<?>> listener = mock(Listener.class);
		scanner.scan(listener);
		verify(listener, times(2)).handle(any(Class.class));
		verify(listener).handle(Resource1.class);
		verify(listener).handle(Resource2.class);
	}
}

@Resource(value=Model1.class)
class Resource1 {
}

@Resource(value=Model2.class)
class Resource2 {
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