package com.flexicore.examples.interfaces;

import com.flexicore.example.person.Person;
import com.flexicore.examples.request.PersonCreate;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.security.SecurityContext;

public interface IPersonService extends ServicePlugin {
	Person createPersonNoMerge(PersonCreate personCreate,
			SecurityContext securityContext);

	boolean updatePersonNoMerge(Person person, PersonCreate personCreate);
}
