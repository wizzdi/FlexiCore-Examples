package com.flexicore.examples.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.example.library.model.Author;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class BookCreate extends BasicCreate {

	private String authorId;
	@JsonIgnore
	private Author author;

	public String getAuthorId() {
		return authorId;
	}

	public <T extends BookCreate> T setAuthorId(String authorId) {
		this.authorId = authorId;
		return (T) this;
	}

	@JsonIgnore
	public Author getAuthor() {
		return author;
	}

	public <T extends BookCreate> T setAuthor(Author author) {
		this.author = author;
		return (T) this;
	}
}
