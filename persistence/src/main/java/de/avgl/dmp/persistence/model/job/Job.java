package de.avgl.dmp.persistence.model.job;

import java.util.Set;

import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;

import de.avgl.dmp.persistence.model.ExtendedBasicDMPJPAObject;

/**
 * @author tgaengler
 */
@XmlRootElement
public class Job extends ExtendedBasicDMPJPAObject {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	//@JsonSerialize(using = SetMappingReferenceSerializer.class)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	//@XmlIDREF
	@XmlList
	private Set<Mapping>		mappings;

	public Set<Mapping> getMappings() {

		return mappings;
	}

	public void setMappings(final Set<Mapping> mappingsArg) {

		mappings = mappingsArg;
	}
}
