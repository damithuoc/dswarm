package org.dswarm.persistence.service;

import javax.persistence.EntityManager;

import com.google.inject.Provider;

import org.dswarm.persistence.model.DMPJPAObject;
import org.dswarm.persistence.model.proxy.ProxyDMPJPAObject;

/**
 * A generic persistence service implementation for {@link DMPJPAObject}s, i.e., objects where the identifier will be generated by
 * the database.
 * 
 * @author tgaengler
 * @param <POJOCLASS> the concrete POJO class
 */
public abstract class BasicIDJPAService<PROXYPOJOCLASS extends ProxyDMPJPAObject<POJOCLASS>, POJOCLASS extends DMPJPAObject> extends
		BasicJPAService<PROXYPOJOCLASS, POJOCLASS, Long> {

	/**
	 * Creates a new persistence service for the given concrete POJO class and the entity manager provider.
	 * 
	 * @param clasz a concrete POJO class
	 * @param entityManagerProvider an entity manager provider
	 */
	protected BasicIDJPAService(final Class<POJOCLASS> clasz, final Class<PROXYPOJOCLASS> proxyClasz,
			final Provider<EntityManager> entityManagerProvider) {

		super(clasz, proxyClasz, entityManagerProvider);
	}
}
