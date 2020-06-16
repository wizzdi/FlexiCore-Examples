package com.flexicore.examples.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.example.library.model.Author;
import com.flexicore.example.library.model.Author_;
import com.flexicore.example.library.model.Book;
import com.flexicore.example.library.model.Book_;
import com.flexicore.examples.request.BookFilter;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

@PluginInfo(version = 1)
@Extension
@Component
public class BookRepository extends AbstractRepositoryPlugin {

	public List<Book> listAllBooks(BookFilter filtering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Book> q = cb.createQuery(Book.class);
		Root<Book> r = q.from(Book.class);
		List<Predicate> preds = new ArrayList<>();
		addBookPredicate(filtering, cb, r, preds);
		QueryInformationHolder<Book> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Book.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addBookPredicate(BookFilter filtering, CriteriaBuilder cb,
			Root<Book> r, List<Predicate> preds) {
		if (filtering.getAuthors() != null && !filtering.getAuthors().isEmpty()) {
			Set<String> ids = filtering.getAuthors().parallelStream()
					.map(f -> f.getId()).collect(Collectors.toSet());
			Join<Book, Author> join = r.join(Book_.author);
			preds.add(join.get(Author_.id).in(ids));
		}
	}

	public Long countAllBooks(BookFilter filtering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Book> r = q.from(Book.class);
		List<Predicate> preds = new ArrayList<>();
		addBookPredicate(filtering, cb, r, preds);
		QueryInformationHolder<Book> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Book.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

}