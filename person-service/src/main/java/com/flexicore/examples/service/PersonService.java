package com.flexicore.examples.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.example.person.Person;
import com.flexicore.examples.data.PersonRepository;
import com.flexicore.examples.request.PersonCreate;
import com.flexicore.examples.request.PersonFilter;
import com.flexicore.examples.request.PersonUpdate;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@PluginInfo(version = 1)
@Component
@Extension
@Primary
public class PersonService implements ServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private PersonRepository repository;


	public Person createPerson(PersonCreate personCreate,
			SecurityContext securityContext) {
		Person person = createPersonNoMerge(personCreate, securityContext);
		repository.merge(person);
		return person;
	}

	public Person createPersonNoMerge(PersonCreate personCreate,
			SecurityContext securityContext) {
		Person person = new Person(personCreate.getFirstName(), securityContext);
		updatePersonNoMerge(person, personCreate);
		return person;
	}

	public boolean updatePersonNoMerge(Person person, PersonCreate personCreate) {
		boolean update = false;
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
			SecurityContext securityContext) {
		Person person = personUpdate.getPerson();
		if (updatePersonNoMerge(person, personUpdate)) {
			repository.merge(person);
		}
		return person;
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
			List<String> batchString, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batchString, securityContext);
	}

	public PaginationResponse<Person> getAllPersons(PersonFilter personFilter,
			SecurityContext securityContext) {
		List<Person> list = listAllPersons(personFilter, securityContext);
		long count = repository.countAllPersons(personFilter, securityContext);
		return new PaginationResponse<>(list, personFilter, count);
	}

	public List<Person> listAllPersons(PersonFilter personFilter,
			SecurityContext securityContext) {
		return repository.listAllPersons(personFilter, securityContext);
	}

}