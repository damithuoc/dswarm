/**
 * Copyright (C) 2013 – 2015 SLUB Dresden & Avantgarde Labs GmbH (<code@dswarm.org>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dswarm.controller.resources;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Provider;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dswarm.controller.DMPControllerException;
import org.dswarm.persistence.dto.BasicDMPDTO;
import org.dswarm.persistence.model.ExtendedBasicDMPJPAObject;
import org.dswarm.persistence.model.proxy.ProxyExtendedBasicDMPJPAObject;
import org.dswarm.persistence.service.ExtendedMediumBasicDMPJPAService;

/**
 * A generic resource (controller service) implementation for {@link org.dswarm.persistence.model.BasicDMPJPAObject}s, i.e., objects where the identifier will
 * be generated by the database and that can have a name and a description.
 *
 * @param <POJOCLASSPERSISTENCESERVICE> the concrete persistence service of the resource that is related to the concrete POJO
 *                                      class
 * @param <POJOCLASS>                   the concrete POJO class of the resource
 * @author tgaengler
 */
public abstract class ExtendedMediumBasicDMPResource<
		POJOCLASSPERSISTENCESERVICE extends ExtendedMediumBasicDMPJPAService<PROXYPOJOCLASS, POJOCLASS, MEDIUMCLASS>,
		PROXYPOJOCLASS extends ProxyExtendedBasicDMPJPAObject<POJOCLASS>,
		POJOCLASS extends ExtendedBasicDMPJPAObject,
		MEDIUMCLASS extends BasicDMPDTO<MEDIUMCLASS>
		> extends ExtendedBasicDMPResource<POJOCLASSPERSISTENCESERVICE, PROXYPOJOCLASS, POJOCLASS> {

	private static final Logger LOG = LoggerFactory.getLogger(ExtendedMediumBasicDMPResource.class);

	/**
	 * Creates a new resource (controller service) for the given concrete POJO class with the provider of the concrete persistence
	 * service, the object mapper and metrics registry.
	 *
	 * @param pojoClassArg                  a concrete POJO class
	 * @param persistenceServiceProviderArg the concrete persistence service that is related to the concrete POJO class
	 * @param objectMapperProviderArg       an object mapper
	 */
	public ExtendedMediumBasicDMPResource(
			final Class<POJOCLASS> pojoClassArg,
			final Provider<POJOCLASSPERSISTENCESERVICE> persistenceServiceProviderArg,
			final Provider<ObjectMapper> objectMapperProviderArg) {

		super(pojoClassArg, persistenceServiceProviderArg, objectMapperProviderArg);
	}

	/**
	 * This endpoint returns an object of the type of the POJO class as JSON representation for the provided object uuid.
	 * The format of the object might either be a full or an abbreviated short variant.
	 *
	 * @param uuid an object uuid
	 * @param format an enum that specifies which format to use
	 * @return a JSON representation of an object of the type of the POJO class
	 */
	@Override
	public Response getObject(
			   /* @PathParam("uuid") */ final String uuid,
			/* @QueryParam("format") */ final POJOFormat format) throws DMPControllerException {

		switch (format) {
			case MEDIUM:
				return getMediumObject(uuid);
			default:
				return super.getObject(uuid, format);
		}
	}

	/**
	 * This endpoint returns a list of all objects of the type of the POJO class as JSON representation.
	 * The format of the objects might either be a full or an abbreviated short variant.
	 *
	 * @param format an enum that specifies which format to use
	 * @return a list of all objects of the type of the POJO class as JSON representation
	 * @throws org.dswarm.controller.DMPControllerException
	 */
	@Override
	public Response getObjects(final POJOFormat format) throws DMPControllerException {

		switch (format) {
			case MEDIUM:
				return getMediumObjects();
			default:
				return super.getObjects(format);
		}
	}

	private Response getMediumObject(final String uuid) throws DMPControllerException {

		LOG.debug("try to get {} with uuid '{}'", pojoClassName, uuid);

		final POJOCLASSPERSISTENCESERVICE persistenceService = persistenceServiceProvider.get();
		final POJOCLASS object = persistenceService.getObject(uuid);

		if (object == null) {

			LOG.debug("couldn't find {} '{}'", pojoClassName, uuid);

			return Response.status(Response.Status.NOT_FOUND).build();
		}
		LOG.debug("got {} with uuid '{}'", pojoClassName, uuid);
		if (LOG.isTraceEnabled()) {
			LOG.trace(" = '{}'", ToStringBuilder.reflectionToString(object));
		}

		final String objectJSON = serializeMediumObject(object);

		LOG.debug("return {} with uuid '{}'", pojoClassName, uuid);
		if (LOG.isTraceEnabled()) {
			LOG.trace(" = '{}'", objectJSON);
		}

		return buildResponse(objectJSON);
	}

	private Response getMediumObjects() throws DMPControllerException {

		LOG.debug("try to get all {}s", pojoClassName);

		final POJOCLASSPERSISTENCESERVICE persistenceService = persistenceServiceProvider.get();

		final List<MEDIUMCLASS> objects = persistenceService.getMediumObjects();

		if (objects == null) {

			LOG.debug("couldn't find {}s", pojoClassName);
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		if (objects.isEmpty()) {

			LOG.debug("there are no {}s", pojoClassName);
			return Response.status(Response.Status.NOT_FOUND).build();
		}

		LOG.debug("got all {}s ", pojoClassName);
		if (LOG.isTraceEnabled()) {
			LOG.trace(" = '{}'", ToStringBuilder.reflectionToString(objects));
		}

		final List<MEDIUMCLASS> dtos = objects.stream()
				.map(dto -> dto.withHref(createObjectURI(dto.uuid)))
				.collect(Collectors.toList());

		final String objectsJSON = serializeObject(dtos);

		LOG.debug("return all {}s ", pojoClassName);
		if (LOG.isTraceEnabled()) {
			LOG.trace("'{}'", objectsJSON);
		}

		return buildResponse(objectsJSON);
	}

	private String serializeMediumObject(final POJOCLASS pojo) throws DMPControllerException {

		final MEDIUMCLASS dto = mediumVersionOf(pojo);
		return serializeObject(dto);
	}

	private MEDIUMCLASS mediumVersionOf(final POJOCLASS pojo) {
		return persistenceServiceProvider.get()
				.createMediumVariant(pojo)
				.withHref(createObjectURI(pojo));
	}
}
