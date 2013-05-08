/**
 * 
 */
package org.minnal.jpa;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolverHolder;
import javax.persistence.spi.PersistenceUnitInfo;

import org.activejpa.jpa.JPA;
import org.minnal.core.Application;
import org.minnal.core.MinnalException;
import org.minnal.core.Plugin;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.DatabaseConfiguration;

/**
 * @author ganeshs
 *
 */
public class JPAPlugin implements Plugin {
	
	private EntityManagerFactory factory;

	public void init(Application<? extends ApplicationConfiguration> application) {
		DatabaseConfiguration configuration = application.getConfiguration().getDatabaseConfiguration();
		List<PersistenceProvider> providers = PersistenceProviderResolverHolder.getPersistenceProviderResolver().getPersistenceProviders();
		if (providers == null || providers.isEmpty()) {
			throw new MinnalException("No JPA persistence provider found");
		}
		PersistenceProvider provider = providers.get(0);
		PersistenceUnitInfo info = new MinnalPersistenceUnitInfo(application.getConfiguration().getName(), configuration, provider); 
		factory = provider.createContainerEntityManagerFactory(info, null);
		JPA.instance.addPersistenceUnit(info.getPersistenceUnitName(), factory);
	}

	public void destroy() {
		factory.close(); 
	}

}
