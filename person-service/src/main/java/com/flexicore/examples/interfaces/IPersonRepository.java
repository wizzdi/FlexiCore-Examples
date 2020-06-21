package com.flexicore.examples.interfaces;

import com.flexicore.example.person.Person;
import com.flexicore.examples.request.PersonFilter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public interface IPersonRepository {


    static <T extends Person> void addPersonPredicate(PersonFilter filtering,
                                                      CriteriaBuilder cb, Root<T> r, List<Predicate> preds) {

    }
}
