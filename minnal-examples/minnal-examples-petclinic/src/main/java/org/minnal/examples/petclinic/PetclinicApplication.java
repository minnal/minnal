package org.minnal.examples.petclinic;

import org.minnal.core.Application;
import org.minnal.jpa.JPAPlugin;
import org.minnal.jpa.OpenSessionInViewFilter;

public class PetclinicApplication extends Application<PetclinicConfiguration> {

	@Override
	protected void registerPlugins() {
		registerPlugin(new JPAPlugin());
	}
	
	@Override
	protected void addFilters() {
		addFilter(new OpenSessionInViewFilter(getConfiguration().getDatabaseConfiguration()));
	}

	@Override
	protected void defineResources() {
	}
}