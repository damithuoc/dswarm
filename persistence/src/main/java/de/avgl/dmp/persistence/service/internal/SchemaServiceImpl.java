package de.avgl.dmp.persistence.service.internal;

import com.google.common.base.Optional;
import com.google.inject.Singleton;

import de.avgl.dmp.persistence.model.jsonschema.JSRoot;
import de.avgl.dmp.persistence.service.internal.memorydb.BaseMemoryServiceImpl;


@Singleton
public class SchemaServiceImpl extends BaseMemoryServiceImpl<Long, Long, JSRoot> {

	public Optional<JSRoot> getSchema(final Long id, final Long configurationId) {
		return getObjects(id, configurationId);
	}
}