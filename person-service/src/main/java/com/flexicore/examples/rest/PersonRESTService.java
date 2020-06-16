package com.flexicore.examples.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.example.person.Person;
import com.flexicore.examples.request.PersonCreate;
import com.flexicore.examples.request.PersonFilter;
import com.flexicore.examples.request.PersonUpdate;
import com.flexicore.examples.service.PersonService;

import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Asaf on 04/06/2017.
 */
@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/Person")
@Tag(name = "Person")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Extension
@Component
public class PersonRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private PersonService service;

	@POST
	@Path("createPerson")
	@Operation(summary = "createPerson", description = "Creates Person")
	public Person createPerson(
			@HeaderParam("authenticationKey") String authenticationKey,
			PersonCreate personCreate, @Context SecurityContext securityContext) {
		return service.createPerson(personCreate, securityContext);
	}

	@PUT
	@Operation(summary = "updatePerson", description = "Updates Person")
	@Path("updatePerson")
	public void updatePerson(
			@HeaderParam("authenticationKey") String authenticationKey,
			PersonUpdate personUpdate, @Context SecurityContext securityContext) {
		String personId = personUpdate.getId();
		Person person = personId != null ? service.getByIdOrNull(personId,
				Person.class, null, securityContext) : null;
		if (person == null) {
			throw new BadRequestException("No Person with id " + personId);
		}
		personUpdate.setPerson(person);
		service.updatePerson(personUpdate, securityContext);
	}

	@POST
	@Operation(summary = "getAllPersons", description = "Gets All Persons Filtered")
	@Path("getAllPersons")
	public PaginationResponse<Person> getAllPersons(
			@HeaderParam("authenticationKey") String authenticationKey,
			PersonFilter personFilter, @Context SecurityContext securityContext) {
		return service.getAllPersons(personFilter, securityContext);
	}
}
