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
import com.flexicore.security.SecurityContextBase;
import com.flexicore.service.BaseclassNewService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@Extension
@Primary
public class PersonService implements ServicePlugin {

	
	@Autowired
	private PersonRepository repository;

	@Autowired
	private BaseclassNewService baseclassNewService;


	public Person createPerson(PersonCreate personCreate,
			SecurityContextBase securityContextBase) {
		Person person = createPersonNoMerge(personCreate, securityContextBase);
		repository.merge(person);
		return person;
	}

	public Person createPersonNoMerge(PersonCreate personCreate,
			SecurityContextBase securityContextBase) {
		Person person = new Person(personCreate.getFirstName(), securityContextBase);
		updatePersonNoMerge(person, personCreate);
		return person;
	}

	public boolean updatePersonNoMerge(Person person, PersonCreate personCreate) {
		boolean update = baseclassNewService.updateBaseclassNoMerge(personCreate,person);
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
			SecurityContextBase securityContextBase) {
		Person person = personUpdate.getPerson();
		if (updatePersonNoMerge(person, personUpdate)) {
			repository.merge(person);
		}
		return person;
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
			List<String> batchString, SecurityContextBase securityContextBase) {
		return repository.getByIdOrNull(id, c, batchString, securityContextBase);
	}

	public PaginationResponse<Person> getAllPersons(PersonFilter personFilter,
			SecurityContextBase securityContextBase) {
		List<Person> list = listAllPersons(personFilter, securityContextBase);
		long count = repository.countAllPersons(personFilter, securityContextBase);
		return new PaginationResponse<>(list, personFilter, count);
	}

	public List<Person> listAllPersons(PersonFilter personFilter,
			SecurityContextBase securityContextBase) {
		return repository.listAllPersons(personFilter, securityContextBase);
	}

}