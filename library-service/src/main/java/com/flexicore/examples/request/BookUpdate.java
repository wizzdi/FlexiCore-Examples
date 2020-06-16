package com.flexicore.examples.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.example.library.model.Book;

public class BookUpdate extends BookCreate {

	private String id;
	@JsonIgnore
	private Book book;

	public String getId() {
		return id;
	}

	public <T extends BookUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public Book getBook() {
		return book;
	}

	public <T extends BookUpdate> T setBook(Book book) {
		this.book = book;
		return (T) this;
	}
}
