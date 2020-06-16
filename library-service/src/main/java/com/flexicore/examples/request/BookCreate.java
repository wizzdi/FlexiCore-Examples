package com.flexicore.examples.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.example.library.model.Author;

public class BookCreate {
	private String name;
	private String description;
	private String authorId;
	@JsonIgnore
	private Author author;

	public String getName() {
		return name;
	}

	public <T extends BookCreate> T setName(String name) {
		this.name = name;
		return (T) this;
	}

	public String getDescription() {
		return description;
	}

	public <T extends BookCreate> T setDescription(String description) {
		this.description = description;
		return (T) this;
	}

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
