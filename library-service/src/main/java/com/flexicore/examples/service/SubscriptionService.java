package com.flexicore.examples.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.example.library.model.Author;
import com.flexicore.example.library.model.Book;
import com.flexicore.example.library.model.Subscription;
import com.flexicore.example.person.Person;
import com.flexicore.examples.data.SubscriptionRepository;
import com.flexicore.examples.request.SubscriptionCreate;
import com.flexicore.examples.request.SubscriptionFilter;
import com.flexicore.examples.request.SubscriptionUpdate;
import com.flexicore.interfaces.ServicePlugin;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.ws.rs.BadRequestException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@PluginInfo(version = 1)
@Extension
@Component
public class SubscriptionService implements ServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private SubscriptionRepository repository;
	@Autowired
	private Logger logger;

	public Subscription createSubscription(
			SubscriptionCreate subscriptionCreate,
			SecurityContext securityContext) {
		Subscription subscription = createSubscriptionNoMerge(
				subscriptionCreate, securityContext);
		repository.merge(subscription);
		return subscription;
	}

	public Subscription createSubscriptionNoMerge(
			SubscriptionCreate subscriptionCreate,
			SecurityContext securityContext) {
		Subscription subscription = new Subscription("subscription",securityContext);
		updateSubscriptionNoMerge(subscription, subscriptionCreate);
		return subscription;
	}

	public boolean updateSubscriptionNoMerge(Subscription subscription,
			SubscriptionCreate subscriptionCreate) {
		boolean update = false;

		if (subscriptionCreate.getDescription() != null
				&& !subscriptionCreate.getDescription().equals(
						subscription.getDescription())) {
			subscription.setDescription(subscriptionCreate.getDescription());
			update = true;
		}

		if (subscriptionCreate.getEndTime() != null
				&& !subscriptionCreate.getEndTime().equals(
						subscription.getEndTime())) {
			subscription.setEndTime(subscriptionCreate.getEndTime());
			update = true;
		}

		if (subscriptionCreate.getStartTime() != null
				&& !subscriptionCreate.getStartTime().equals(
						subscription.getStartTime())) {
			subscription.setStartTime(subscriptionCreate.getStartTime());
			update = true;
		}
		if (subscriptionCreate.getPerson() != null
				&& (subscription.getPerson() == null || !subscriptionCreate
						.getPerson().getId()
						.equals(subscription.getPerson().getId()))) {
			subscription.setPerson(subscriptionCreate.getPerson());
			update = true;
		}

		if (subscriptionCreate.getBook() != null
				&& (subscription.getBook() == null || !subscriptionCreate
						.getBook().getId()
						.equals(subscription.getBook().getId()))) {
			subscription.setBook(subscriptionCreate.getBook());
			update = true;
		}

		return update;
	}

	public Subscription updateSubscription(
			SubscriptionUpdate subscriptionUpdate,
			SecurityContext securityContext) {
		Subscription subscription = subscriptionUpdate.getSubscription();
		if (updateSubscriptionNoMerge(subscription, subscriptionUpdate)) {
			repository.merge(subscription);
		}
		return subscription;
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
			List<String> batchString, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batchString, securityContext);
	}

	public PaginationResponse<Subscription> getAllSubscriptions(
			SubscriptionFilter subscriptionFilter,
			SecurityContext securityContext) {
		List<Subscription> list = listAllSubscriptions(subscriptionFilter,
				securityContext);
		long count = repository.countAllSubscriptions(subscriptionFilter,
				securityContext);
		return new PaginationResponse<>(list, subscriptionFilter, count);
	}

	public List<Subscription> listAllSubscriptions(
			SubscriptionFilter subscriptionFilter,
			SecurityContext securityContext) {
		return repository.listAllSubscriptions(subscriptionFilter,
				securityContext);
	}

	public void validate(SubscriptionFilter subscriptionFilter,
			SecurityContext securityContext) {
		Set<String> bookIds = subscriptionFilter.getBookIds();
		Map<String, Book> bookMap = bookIds.isEmpty()
				? new HashMap<>()
				: repository.listByIds(Book.class, bookIds, securityContext)
						.parallelStream()
						.collect(Collectors.toMap(f -> f.getId(), f -> f));
		bookIds.removeAll(bookMap.keySet());
		if (!bookIds.isEmpty()) {
			throw new BadRequestException("No Books with ids " + bookIds);
		}
		subscriptionFilter.setBooks(new ArrayList<>(bookMap.values()));

		Set<String> personIds = subscriptionFilter.getPersonIds();
		Map<String, Person> personMap = personIds.isEmpty()
				? new HashMap<>()
				: repository
						.listByIds(Person.class, personIds, securityContext)
						.parallelStream()
						.collect(Collectors.toMap(f -> f.getId(), f -> f));
		personIds.removeAll(personMap.keySet());
		if (!personIds.isEmpty()) {
			throw new BadRequestException("No Person with ids " + personIds);
		}
		subscriptionFilter.setPersons(new ArrayList<>(personMap.values()));
	}

	public void validate(SubscriptionCreate subscriptionCreate,
			SecurityContext securityContext) {
		String bookId = subscriptionCreate.getBookId();
		Book book = bookId != null ? getByIdOrNull(bookId, Book.class, null,
				securityContext) : null;
		if (bookId!=null&&book == null) {
			throw new BadRequestException("No Book with id " + bookId);
		}
		subscriptionCreate.setBook(book);

		String personId = subscriptionCreate.getPersonId();
		Person person = personId != null ? getByIdOrNull(personId,
				Person.class, null, securityContext) : null;
		if (personId!=null&&person == null) {
			throw new BadRequestException("No Person with id " + personId);
		}
		subscriptionCreate.setPerson(person);
	}
}