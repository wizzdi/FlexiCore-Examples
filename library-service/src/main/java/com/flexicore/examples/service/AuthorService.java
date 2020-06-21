package com.flexicore.examples.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.example.library.model.Author;
import com.flexicore.examples.data.AuthorRepository;
import com.flexicore.examples.interfaces.IPersonService;
import com.flexicore.examples.request.AuthorCreate;
import com.flexicore.examples.request.AuthorFilter;
import com.flexicore.examples.request.AuthorUpdate;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

@PluginInfo(version = 1)
@Component
@Extension
@Primary
public class AuthorService implements ServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private AuthorRepository repository;
	@Autowired
	private Logger logger;

	@PluginInfo(version = 1)
	@Autowired
	private IPersonService personService;

	public Author createAuthor(AuthorCreate authorCreate,
			SecurityContext securityContext) {
		Author author = createAuthorNoMerge(authorCreate, securityContext);
		repository.merge(author);
		return author;
	}

	public Author createAuthorNoMerge(AuthorCreate authorCreate,
			SecurityContext securityContext) {
		Author author = new Author(authorCreate.getFirstName(),securityContext);
		updateAuthorNoMerge(author, authorCreate);
		return author;
	}

	public boolean updateAuthorNoMerge(Author author, AuthorCreate authorCreate) {
		boolean update =personService.updatePersonNoMerge(author,authorCreate);

		return update;
	}

	public Author updateAuthor(AuthorUpdate authorUpdate,
			SecurityContext securityContext) {
		Author author = authorUpdate.getAuthor();
		if (updateAuthorNoMerge(author, authorUpdate)) {
			repository.merge(author);
		}
		return author;
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
			List<String> batchString, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batchString, securityContext);
	}

	public PaginationResponse<Author> getAllAuthors(AuthorFilter authorFilter,
			SecurityContext securityContext) {
		List<Author> list = listAllAuthors(authorFilter, securityContext);
		long count = repository.countAllAuthors(authorFilter, securityContext);
		return new PaginationResponse<>(list, authorFilter, count);
	}

	public List<Author> listAllAuthors(AuthorFilter authorFilter,
			SecurityContext securityContext) {
		return repository.listAllAuthors(authorFilter, securityContext);
	}

	public void validate(AuthorFilter authorFilter,
			SecurityContext securityContext) {

	}

	public void validate(AuthorCreate authorCreate,
			SecurityContext securityContext) {

	}
}