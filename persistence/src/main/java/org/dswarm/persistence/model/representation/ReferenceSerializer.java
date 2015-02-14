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
package org.dswarm.persistence.model.representation;

import java.io.IOException;

import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.dswarm.persistence.model.DMPObject;

public abstract class ReferenceSerializer<DMPOBJECTIMPL extends DMPObject> extends JsonSerializer<DMPOBJECTIMPL> {

	@Override
	public void serialize(final DMPOBJECTIMPL object, final JsonGenerator generator, final SerializerProvider provider) throws IOException {

		if (object == null) {

			generator.writeNull();

			return;
		}

		final Reference<String> reference = new Reference<>(object.getUuid());

		generator.writeObject(reference);
	}

	@XmlRootElement
	static class Reference<IDTYPE> {

		@XmlID
		private final IDTYPE uuid;

		Reference(final IDTYPE uuidArg) {

			uuid = uuidArg;
		}

		IDTYPE getUuid() {

			return uuid;
		}
	}

}
