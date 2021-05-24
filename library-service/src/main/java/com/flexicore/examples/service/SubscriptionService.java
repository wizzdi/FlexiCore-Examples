package com.flexicore.examples.service;

import com.flexicore.example.library.model.Book;
import com.flexicore.example.library.model.Book_;
import com.flexicore.example.library.model.Subscription;
import com.flexicore.example.person.Person;
import com.flexicore.example.person.Person_;
import com.flexicore.examples.data.SubscriptionRepository;
import com.flexicore.examples.request.SubscriptionCreate;
import com.flexicore.examples.request.SubscriptionFilter;
import com.flexicore.examples.request.SubscriptionUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;


@Extension
@Component
public class SubscriptionService implements Plugin {

	
	@Autowired
	private SubscriptionRepository repository;
	@Autowired
	private BasicService basicService;

	public Subscription createSubscription(
			SubscriptionCreate subscriptionCreate,
			SecurityContextBase securityContext) {
		Subscription subscription = createSubscriptionNoMerge(
				subscriptionCreate, securityContext);
		repository.merge(subscription);
		return subscription;
	}

	public Subscription createSubscriptionNoMerge(
			SubscriptionCreate subscriptionCreate,
			SecurityContextBase securityContext) {
		Subscription subscription = new Subscription();
		subscription.setId(UUID.randomUUID().toString());
		updateSubscriptionNoMerge(subscription, subscriptionCreate);
		BaseclassService.createSecurityObjectNoMerge(subscription,securityContext);
		return subscription;
	}

	public boolean updateSubscriptionNoMerge(Subscription subscription,
			SubscriptionCreate subscriptionCreate) {
		boolean update = basicService.updateBasicNoMerge(subscriptionCreate,subscription);


		if (subscriptionCreate.getEndTime() != null && !subscriptionCreate.getEndTime().equals(subscription.getEndTime())) {
			subscription.setEndTime(subscriptionCreate.getEndTime());
			update = true;
		}

		if (subscriptionCreate.getStartTime() != null && !subscriptionCreate.getStartTime().equals(subscription.getStartTime())) {
			subscription.setStartTime(subscriptionCreate.getStartTime());
			update = true;
		}
		if (subscriptionCreate.getPerson() != null && (subscription.getPerson() == null || !subscriptionCreate.getPerson().getId().equals(subscription.getPerson().getId()))) {
			subscription.setPerson(subscriptionCreate.getPerson());
			update = true;
		}

		if (subscriptionCreate.getBook() != null && (subscription.getBook() == null || !subscriptionCreate.getBook().getId().equals(subscription.getBook().getId()))) {
			subscription.setBook(subscriptionCreate.getBook());
			update = true;
		}

		return update;
	}

	public Subscription updateSubscription(SubscriptionUpdate subscriptionUpdate, SecurityContextBase securityContext) {
		Subscription subscription = subscriptionUpdate.getSubscription();
		if (updateSubscriptionNoMerge(subscription, subscriptionUpdate)) {
			repository.merge(subscription);
		}
		return subscription;
	}

	public PaginationResponse<Subscription> getAllSubscriptions(
			SubscriptionFilter subscriptionFilter,
			SecurityContextBase securityContext) {
		List<Subscription> list = listAllSubscriptions(subscriptionFilter,
				securityContext);
		long count = repository.countAllSubscriptions(subscriptionFilter,
				securityContext);
		return new PaginationResponse<>(list, subscriptionFilter, count);
	}

	public List<Subscription> listAllSubscriptions(
			SubscriptionFilter subscriptionFilter,
			SecurityContextBase securityContext) {
		return repository.listAllSubscriptions(subscriptionFilter,
				securityContext);
	}

	public void validate(SubscriptionFilter subscriptionFilter,
			SecurityContextBase securityContext) {
		Set<String> bookIds = subscriptionFilter.getBookIds();
		Map<String, Book> bookMap = bookIds.isEmpty()
				? new HashMap<>()
				: repository.listByIds(Book.class, bookIds, Book_.security, securityContext)
						.parallelStream()
						.collect(Collectors.toMap(f -> f.getId(), f -> f));
		bookIds.removeAll(bookMap.keySet());
		if (!bookIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Books with ids " + bookIds);
		}
		subscriptionFilter.setBooks(new ArrayList<>(bookMap.values()));

		Set<String> personIds = subscriptionFilter.getPersonIds();
		Map<String, Person> personMap = personIds.isEmpty()
				? new HashMap<>()
				: repository
						.listByIds(Person.class, personIds, Person_.security, securityContext)
						.parallelStream()
						.collect(Collectors.toMap(f -> f.getId(), f -> f));
		personIds.removeAll(personMap.keySet());
		if (!personIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Person with ids " + personIds);
		}
		subscriptionFilter.setPersons(new ArrayList<>(personMap.values()));
	}

	public void validate(SubscriptionCreate subscriptionCreate,
			SecurityContextBase securityContext) {
		String bookId = subscriptionCreate.getBookId();
		Book book = bookId != null ? repository.getByIdOrNull(bookId, Book.class, Book_.security,
				securityContext) : null;
		if (bookId!=null&&book == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Book with id " + bookId);
		}
		subscriptionCreate.setBook(book);

		String personId = subscriptionCreate.getPersonId();
		Person person = personId != null ? repository.getByIdOrNull(personId,
				Person.class, Person_.security, securityContext) : null;
		if (personId!=null&&person == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Person with id " + personId);
		}
		subscriptionCreate.setPerson(person);
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