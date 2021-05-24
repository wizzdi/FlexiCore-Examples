package com.flexicore.examples.service;

import com.flexicore.example.person.Person;
import com.flexicore.examples.data.PersonRepository;
import com.flexicore.examples.request.PersonCreate;
import com.flexicore.examples.request.PersonFilter;
import com.flexicore.examples.request.PersonUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.UUID;


@Service
@Extension
public class PersonService implements Plugin {

	
	@Autowired
	private PersonRepository repository;

	@Autowired
	private BasicService basicService;


	public Person createPerson(PersonCreate personCreate,
			SecurityContextBase securityContext) {
		Person person = createPersonNoMerge(personCreate, securityContext);
		repository.merge(person);
		return person;
	}

	public Person createPersonNoMerge(PersonCreate personCreate,
			SecurityContextBase securityContext) {
		Person person = new Person();
		person.setId(UUID.randomUUID().toString());
		updatePersonNoMerge(person, personCreate);
		BaseclassService.createSecurityObjectNoMerge(person,securityContext);
		return person;
	}

	public boolean updatePersonNoMerge(Person person, PersonCreate personCreate) {
		boolean update = basicService.updateBasicNoMerge(personCreate,person);
		if (personCreate.getFirstName() != null
				&& !personCreate.getFirstName().equals(person.getFirstName())) {
			person.setFirstName(personCreate.getFirstName());
			update = true;
		}

		if (personCreate.getLastName() != null
				&& !personCreate.getLastName().equals(person.getLastName())) {
			person.setLastName(personCreate.getLastName());
			update = true;
		}

		return update;
	}

	public Person updatePerson(PersonUpdate personUpdate,
			SecurityContextBase securityContext) {
		Person person = personUpdate.getPerson();
		if (updatePersonNoMerge(person, personUpdate)) {
			repository.merge(person);
		}
		return person;
	}


	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public PaginationResponse<Person> getAllPersons(PersonFilter personFilter,
													SecurityContextBase securityContext) {
		List<Person> list = listAllPersons(personFilter, securityContext);
		long count = repository.countAllPersons(personFilter, securityContext);
		return new PaginationResponse<>(list, personFilter, count);
	}

	public List<Person> listAllPersons(PersonFilter personFilter,
			SecurityContextBase securityContext) {
		return repository.listAllPersons(personFilter, securityContext);
	}

}