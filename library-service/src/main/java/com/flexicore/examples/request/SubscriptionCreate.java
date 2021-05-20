package com.flexicore.examples.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.example.library.model.Book;
import com.flexicore.example.person.Person;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.time.OffsetDateTime;

public class SubscriptionCreate extends BasicCreate {

	private OffsetDateTime startTime;
	private OffsetDateTime endTime;
	private String bookId;
	@JsonIgnore
	private Book book;
	private String personId;
	@JsonIgnore
	private Person person;


	public OffsetDateTime getStartTime() {
		return startTime;
	}

	public <T extends SubscriptionCreate> T setStartTime(OffsetDateTime startTime) {
		this.startTime = startTime;
		return (T) this;
	}

	public OffsetDateTime getEndTime() {
		return endTime;
	}

	public <T extends SubscriptionCreate> T setEndTime(OffsetDateTime endTime) {
		this.endTime = endTime;
		return (T) this;
	}

	public String getBookId() {
		return bookId;
	}

	public <T extends SubscriptionCreate> T setBookId(String bookId) {
		this.bookId = bookId;
		return (T) this;
	}

	@JsonIgnore
	public Book getBook() {
		return book;
	}

	public <T extends SubscriptionCreate> T setBook(Book book) {
		this.book = book;
		return (T) this;
	}

	public String getPersonId() {
		return personId;
	}

	public <T extends SubscriptionCreate> T setPersonId(String personId) {
		this.personId = personId;
		return (T) this;
	}

	@JsonIgnore
	public Person getPerson() {
		return person;
	}

	public <T extends SubscriptionCreate> T setPerson(Person person) {
		this.person = person;
		return (T) this;
	}
}
