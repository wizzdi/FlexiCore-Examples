package com.flexicore.examples.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.example.person.Person;

public class PersonUpdate extends PersonCreate {

	private String id;
	@JsonIgnore
	private Person person;

	public String getId() {
		return id;
	}

	public <T extends PersonUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public Person getPerson() {
		return person;
	}

	public <T extends PersonUpdate> T setPerson(Person person) {
		this.person = person;
		return (T) this;
	}
}
