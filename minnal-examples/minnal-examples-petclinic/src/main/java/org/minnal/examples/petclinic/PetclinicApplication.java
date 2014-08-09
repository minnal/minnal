package org.minnal.examples.petclinic;

import org.minnal.core.Application;
import org.minnal.jpa.JPAPlugin;

import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module.Feature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class PetclinicApplication extends Application<PetclinicConfiguration> {

	@Override
	protected void registerPlugins() {
		registerPlugin(new JPAPlugin());
	}

	@Override
	protected void defineResources() {
	}
	
	@Override
	public void init() {
		super.init();
		Hibernate4Module module = new Hibernate4Module();
		module.configure(Feature.FORCE_LAZY_LOADING, true);
		getObjectMapper().registerModule(new JodaModule());
		getObjectMapper().registerModule(module);
	}
}