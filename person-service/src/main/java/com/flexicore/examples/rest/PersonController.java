package com.flexicore.examples.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.example.person.Person;
import com.flexicore.example.person.Person_;
import com.flexicore.examples.request.PersonCreate;
import com.flexicore.examples.request.PersonFilter;
import com.flexicore.examples.request.PersonUpdate;
import com.flexicore.examples.service.PersonService;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Created by Asaf on 04/06/2017.
 */

@RestController
@RequestMapping("/person")
@Extension
@Tag(name = "person")
@OperationsInside
public class PersonController implements Plugin {

	
	@Autowired
	private PersonService service;

	@PostMapping("createPerson")
	@Operation(summary = "createPerson", description = "Creates Person")
	public Person createPerson(
			@RequestHeader(value = "authenticationKey",required = false) String authenticationKey,@RequestBody
			PersonCreate personCreate, @RequestAttribute SecurityContextBase securityContext) {
		return service.createPerson(personCreate, securityContext);
	}

	@Operation(summary = "updatePerson", description = "Updates Person")
	@PutMapping("updatePerson")
	public Person updatePerson(
			@RequestHeader(value = "authenticationKey",required = false) String authenticationKey,@RequestBody
			PersonUpdate personUpdate, @RequestAttribute SecurityContextBase securityContext) {
		String personId = personUpdate.getId();
		Person person = personId != null ? service.getByIdOrNull(personId,
				Person.class, Person_.security, securityContext) : null;
		if (person == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Person with id " + personId);
		}
		personUpdate.setPerson(person);
		return service.updatePerson(personUpdate, securityContext);
	}

	@Operation(summary = "getAllPersons", description = "Gets All Persons Filtered")
	@PostMapping("getAllPersons")
	public PaginationResponse<Person> getAllPersons(
			@RequestHeader(value = "authenticationKey",required = false) String authenticationKey,@RequestBody
			PersonFilter personFilter, @RequestAttribute SecurityContextBase securityContext) {
		return service.getAllPersons(personFilter, securityContext);
	}
}
