package com.flexicore.examples.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.example.person.Person;
import com.flexicore.examples.request.PersonFilter;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContext;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;


@PluginInfo(version = 1)
@Extension
@Component
public class PersonRepository extends AbstractRepositoryPlugin  {

	public List<Person> listAllPersons(PersonFilter filtering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> q = cb.createQuery(Person.class);
		Root<Person> r = q.from(Person.class);
		List<Predicate> preds = new ArrayList<>();
		addPersonPredicate(filtering, cb, r, preds);
		QueryInformationHolder<Person> queryInformationHolder = new QueryInformationHolder<>(filtering, Person.class, securityContext);
		return getAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	public Long countAllPersons(PersonFilter filtering,
			SecurityContext securityContext) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Person> r = q.from(Person.class);
		List<Predicate> preds = new ArrayList<>();
		addPersonPredicate(filtering, cb, r, preds);
		QueryInformationHolder<Person> queryInformationHolder = new QueryInformationHolder<>(filtering, Person.class, securityContext);
		return countAllFiltered(queryInformationHolder, preds, cb, q, r);
	}

	static <T extends Person> void addPersonPredicate(PersonFilter filtering, CriteriaBuilder cb, Root<T> r, List<Predicate> preds) {

	}



}