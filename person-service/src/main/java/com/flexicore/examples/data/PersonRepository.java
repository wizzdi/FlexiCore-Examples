package com.flexicore.examples.data;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.example.person.Person;
import com.flexicore.examples.request.PersonFilter;
import com.flexicore.interfaces.AbstractRepositoryPlugin;
import com.flexicore.model.QueryInformationHolder;
import com.flexicore.security.SecurityContextBase;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Extension
@Component
public class PersonRepository implements Plugin {

	@Autowired
	private EntityManager em;
	@Autowired
	private SecuredBasicRepository securedBasicRepository;

	public List<Person> listAllPersons(PersonFilter filtering,
			SecurityContextBase securityContextBase) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Person> q = cb.createQuery(Person.class);
		Root<Person> r = q.from(Person.class);
		List<Predicate> preds = new ArrayList<>();
		addPersonPredicate(filtering, cb, r, preds);
		q.select(r).where(preds.toArray(new Predicate[0]));
		TypedQuery<Person> query=em.createQuery(q);
		BasicRepository.addPagination(filtering,query);
		return query.getResultList();
	}

	public Long countAllPersons(PersonFilter filtering,
			SecurityContextBase securityContextBase) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> q = cb.createQuery(Long.class);
		Root<Person> r = q.from(Person.class);
		List<Predicate> preds = new ArrayList<>();
		addPersonPredicate(filtering, cb, r, preds);
		securedBasicRepository.addSecuredBasicPredicates(null,cb,q,r,preds,securityContextBase);
		q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
		TypedQuery<Long> query=em.createQuery(q);
		return query.getSingleResult();
	}

	static <T extends Person> void addPersonPredicate(PersonFilter filtering, CriteriaBuilder cb, Root<T> r, List<Predicate> preds) {

	}



}