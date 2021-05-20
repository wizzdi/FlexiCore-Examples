package com.flexicore.examples.service;

import com.flexicore.example.library.model.Author;
import com.flexicore.examples.data.AuthorRepository;
import com.flexicore.examples.request.AuthorCreate;
import com.flexicore.examples.request.AuthorFilter;
import com.flexicore.examples.request.AuthorUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Component
@Extension

public class AuthorService implements Plugin {

	
	@Autowired
	private AuthorRepository repository;
	
	@Autowired
	private PersonService personService;
	public Author createAuthor(AuthorCreate authorCreate,
			SecurityContextBase securityContext) {
		Author author = createAuthorNoMerge(authorCreate, securityContext);
		repository.merge(author);
		return author;
	}

	public Author createAuthorNoMerge(AuthorCreate authorCreate,
			SecurityContextBase securityContext) {
		Author author = new Author();
		author.setId(UUID.randomUUID().toString());
		updateAuthorNoMerge(author, authorCreate);
		BaseclassService.createSecurityObjectNoMerge(author,securityContext);
		return author;
	}

	public boolean updateAuthorNoMerge(Author author, AuthorCreate authorCreate) {
		boolean update =personService.updatePersonNoMerge(author,authorCreate);

		return update;
	}

	public Author updateAuthor(AuthorUpdate authorUpdate,
			SecurityContextBase securityContext) {
		Author author = authorUpdate.getAuthor();
		if (updateAuthorNoMerge(author, authorUpdate)) {
			repository.merge(author);
		}
		return author;
	}


	public PaginationResponse<Author> getAllAuthors(AuthorFilter authorFilter,
													SecurityContextBase securityContext) {
		List<Author> list = listAllAuthors(authorFilter, securityContext);
		long count = repository.countAllAuthors(authorFilter, securityContext);
		return new PaginationResponse<>(list, authorFilter, count);
	}

	public List<Author> listAllAuthors(AuthorFilter authorFilter,
			SecurityContextBase securityContext) {
		return repository.listAllAuthors(authorFilter, securityContext);
	}

	public void validate(AuthorFilter authorFilter,
			SecurityContextBase securityContext) {


	}

	public void validate(AuthorCreate authorCreate,
			SecurityContextBase securityContext) {

	}


	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return repository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return repository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return repository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return repository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		repository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		repository.massMerge(toMerge);
	}
}