package com.flexicore.examples.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.example.library.model.Author;

public class AuthorUpdate extends AuthorCreate {

	private String id;
	@JsonIgnore
	private Author author;

	public String getId() {
		return id;
	}

	public <T extends AuthorUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public Author getAuthor() {
		return author;
	}

	public <T extends AuthorUpdate> T setAuthor(Author author) {
		this.author = author;
		return (T) this;
	}
}
