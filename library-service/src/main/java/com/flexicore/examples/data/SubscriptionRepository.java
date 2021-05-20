package com.flexicore.examples.data;

import com.flexicore.example.library.model.Book;
import com.flexicore.example.library.model.Book_;
import com.flexicore.example.library.model.Subscription;
import com.flexicore.example.library.model.Subscription_;
import com.flexicore.example.person.Person;
import com.flexicore.example.person.Person_;
import com.flexicore.examples.request.SubscriptionFilter;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Extension
@Component
public class SubscriptionRepository implements Plugin {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private SecuredBasicRepository securedBasicRepository;

    public List<Subscription> listAllSubscriptions(
            SubscriptionFilter filtering, SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Subscription> q = cb.createQuery(Subscription.class);
        Root<Subscription> r = q.from(Subscription.class);
        List<Predicate> preds = new ArrayList<>();
        addSubscriptionPredicate(filtering, cb,q, r, preds,securityContext);
        q.select(r).where(preds.toArray(new Predicate[0]));
        TypedQuery<Subscription> query=em.createQuery(q);
        BasicRepository.addPagination(filtering,query);
        return query.getResultList();
    }

    public <T extends Subscription> void addSubscriptionPredicate(SubscriptionFilter filtering,
                                          CriteriaBuilder cb, CommonAbstractCriteria q, From<?,T> r, List<Predicate> preds, SecurityContextBase securityContext) {
        securedBasicRepository.addSecuredBasicPredicates(null,cb,q,r,preds,securityContext);
        if (filtering.getBooks() != null && !filtering.getBooks().isEmpty()) {
            Set<String> ids = filtering.getBooks().parallelStream()
                    .map(f -> f.getId()).collect(Collectors.toSet());
            Join<T, Book> join = r.join(Subscription_.book);
            preds.add(join.get(Book_.id).in(ids));
        }

        if (filtering.getPersons() != null && !filtering.getPersons().isEmpty()) {
            Set<String> ids = filtering.getPersons().parallelStream()
                    .map(f -> f.getId()).collect(Collectors.toSet());
            Join<T, Person> join = r.join(Subscription_.person);
            preds.add(join.get(Person_.id).in(ids));
        }

    }

    public Long countAllSubscriptions(SubscriptionFilter filtering,
                                      SecurityContextBase securityContext) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Subscription> r = q.from(Subscription.class);
        List<Predicate> preds = new ArrayList<>();
        addSubscriptionPredicate(filtering, cb, q, r, preds, securityContext);
        q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
        TypedQuery<Long> query=em.createQuery(q);
        return query.getSingleResult();
    }


    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
        return securedBasicRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
        return securedBasicRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return securedBasicRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
        return securedBasicRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return securedBasicRepository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return securedBasicRepository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return securedBasicRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        securedBasicRepository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        securedBasicRepository.massMerge(toMerge);
    }
}