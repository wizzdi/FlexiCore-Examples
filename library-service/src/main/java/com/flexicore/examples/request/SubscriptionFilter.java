package com.flexicore.examples.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.example.library.model.Book;
import com.flexicore.example.person.Person;
import com.flexicore.model.FilteringInformationHolder;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SubscriptionFilter extends FilteringInformationHolder {

	private Set<String> bookIds = new HashSet<>();
	@JsonIgnore
	private List<Book> books;

	private Set<String> personIds = new HashSet<>();
	@JsonIgnore
	private List<Person> persons;

	public Set<String> getBookIds() {
		return bookIds;
	}

	public <T extends SubscriptionFilter> T setBookIds(Set<String> bookIds) {
		this.bookIds = bookIds;
		return (T) this;
	}

	@JsonIgnore
	public List<Book> getBooks() {
		return books;
	}

	public <T extends SubscriptionFilter> T setBooks(List<Book> books) {
		this.books = books;
		return (T) this;
	}

	public Set<String> getPersonIds() {
		return personIds;
	}

	public <T extends SubscriptionFilter> T setPersonIds(Set<String> personIds) {
		this.personIds = personIds;
		return (T) this;
	}

	@JsonIgnore
	public List<Person> getPersons() {
		return persons;
	}

	public <T extends SubscriptionFilter> T setPersons(List<Person> persons) {
		this.persons = persons;
		return (T) this;
	}

}
