package com.flexicore.examples.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.example.library.model.Author;
import com.flexicore.examples.request.AuthorFilter;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@PluginInfo(version = 1)
@Extension
@Component
public class AuthorRepository extends AbstractRepositoryPlugin {

	public List<Author> listAllAuthors(AuthorFilter filtering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Author> q = cb.createQuery(Author.class);
		Root<Author> r = q.from(Author.class);
		List<Predicate> preds = new ArrayList<>();
		addAuthorPredicate(filtering, cb, r, preds);
		QueryInformationHolder<Author> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Author.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addAuthorPredicate(AuthorFilter filtering, CriteriaBuilder cb,
			Root<Author> r, List<Predicate> preds) {
		PersonRepository.addPersonPredicate(filtering, cb, r, preds);

	}

	public Long countAllAuthors(AuthorFilter filtering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Author> r = q.from(Author.class);
		List<Predicate> preds = new ArrayList<>();
		addAuthorPredicate(filtering, cb, r, preds);
		QueryInformationHolder<Author> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Author.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

}