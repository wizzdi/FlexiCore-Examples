package com.flexicore.examples.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.example.library.model.Book;
import com.flexicore.example.library.model.Book_;
import com.flexicore.example.library.model.Subscription;
import com.flexicore.example.library.model.Subscription_;
import com.flexicore.example.person.Person;
import com.flexicore.example.person.Person_;
import com.flexicore.examples.request.SubscriptionFilter;
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
public class SubscriptionRepository extends AbstractRepositoryPlugin {

	public List<Subscription> listAllSubscriptions(
			SubscriptionFilter filtering, SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Subscription> q = cb.createQuery(Subscription.class);
		Root<Subscription> r = q.from(Subscription.class);
		List<Predicate> preds = new ArrayList<>();
		addSubscriptionPredicate(filtering, cb, r, preds);
		QueryInformationHolder<Subscription> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Subscription.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	private void addSubscriptionPredicate(SubscriptionFilter filtering,
			CriteriaBuilder cb, Root<Subscription> r, List<Predicate> preds) {
		if (filtering.getBooks() != null && !filtering.getBooks().isEmpty()) {
			Set<String> ids = filtering.getBooks().parallelStream()
					.map(f -> f.getId()).collect(Collectors.toSet());
			Join<Subscription, Book> join = r.join(Subscription_.book);
			preds.add(join.get(Book_.id).in(ids));
		}

		if (filtering.getPersons() != null && !filtering.getPersons().isEmpty()) {
			Set<String> ids = filtering.getPersons().parallelStream()
					.map(f -> f.getId()).collect(Collectors.toSet());
			Join<Subscription, Person> join = r.join(Subscription_.person);
			preds.add(join.get(Person_.id).in(ids));
		}

	}

	public Long countAllSubscriptions(SubscriptionFilter filtering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Subscription> r = q.from(Subscription.class);
		List<Predicate> preds = new ArrayList<>();
		addSubscriptionPredicate(filtering, cb, r, preds);
		QueryInformationHolder<Subscription> queryInformationHolder = new QueryInformationHolder<>(
				filtering, Subscription.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

}