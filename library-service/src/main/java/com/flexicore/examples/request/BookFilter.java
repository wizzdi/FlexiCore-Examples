package com.flexicore.examples.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.example.library.model.Author;
import com.flexicore.model.FilteringInformationHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookFilter extends FilteringInformationHolder {

	private Set<String> authorIds = new HashSet<>();
	@JsonIgnore
	private List<Author> authors;

	public Set<String> getAuthorIds() {
		return authorIds;
	}

	public <T extends BookFilter> T setAuthorIds(Set<String> authorIds) {
		this.authorIds = authorIds;
		return (T) this;
	}

	@JsonIgnore
	public List<Author> getAuthors() {
		return authors;
	}

	public <T extends BookFilter> T setAuthors(List<Author> authors) {
		this.authors = authors;
		return (T) this;
	}
}
