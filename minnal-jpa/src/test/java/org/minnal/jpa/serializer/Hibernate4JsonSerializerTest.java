package org.minnal.jpa.serializer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Hibernate4JsonSerializerTest {

	private DummyModel model;
	
	private Hibernate4JsonSerializer serializer;
	
	private LazyInitializer lazyInitializer;

	@BeforeMethod
	public void setup() {
		serializer = new Hibernate4JsonSerializer();
		HibernateProxy proxy = mock(HibernateProxy.class);
		lazyInitializer = mock(LazyInitializer.class);
		when(proxy.getHibernateLazyInitializer()).thenReturn(lazyInitializer);
		model = new DummyModel(proxy);
	}
	
	@Test
	public void shouldLoadProxyOnSerialize() {
		serializer.serialize(model);
		verify(lazyInitializer).getImplementation();
	}

	public static class DummyModel {
		
		private HibernateProxy value;
		
		public DummyModel(HibernateProxy value) {
			this.value = value;
		}
		
		public HibernateProxy getValue() {
			return value;
		}
		
		public void setValue(HibernateProxy value) {
			this.value = value;
		}
	}
}
