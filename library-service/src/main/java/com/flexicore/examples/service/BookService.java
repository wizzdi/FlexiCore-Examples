package com.flexicore.examples.service;

import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.example.library.model.Author;
import com.flexicore.example.library.model.Book;
import com.flexicore.examples.data.BookRepository;
import com.flexicore.examples.request.BookCreate;
import com.flexicore.examples.request.BookFilter;
import com.flexicore.examples.request.BookUpdate;
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
public class BookService implements ServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private BookRepository repository;
	@Autowired
	private Logger logger;

	public Book createBook(BookCreate bookCreate,
			SecurityContext securityContext) {
		Book book = createBookNoMerge(bookCreate, securityContext);
		repository.merge(book);
		return book;
	}

	public Book createBookNoMerge(BookCreate bookCreate,
			SecurityContext securityContext) {
		Book book = new Book(bookCreate.getName(),securityContext);
		updateBookNoMerge(book, bookCreate);
		return book;
	}

	public boolean updateBookNoMerge(Book book, BookCreate bookCreate) {
		boolean update = false;
		if (bookCreate.getName() != null
				&& !bookCreate.getName().equals(book.getName())) {
			book.setName(bookCreate.getName());
			update = true;
		}

		if (bookCreate.getDescription() != null
				&& !bookCreate.getDescription().equals(book.getDescription())) {
			book.setDescription(bookCreate.getDescription());
			update = true;
		}

		if (bookCreate.getAuthor() != null
				&& (book.getAuthor() == null || !bookCreate.getAuthor().getId()
						.equals(book.getAuthor().getId()))) {
			book.setAuthor(bookCreate.getAuthor());
			update = true;
		}

		return update;
	}

	public Book updateBook(BookUpdate bookUpdate,
			SecurityContext securityContext) {
		Book book = bookUpdate.getBook();
		if (updateBookNoMerge(book, bookUpdate)) {
			repository.merge(book);
		}
		return book;
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c,
			List<String> batchString, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, batchString, securityContext);
	}

	public PaginationResponse<Book> getAllBooks(BookFilter bookFilter,
			SecurityContext securityContext) {
		List<Book> list = listAllBooks(bookFilter, securityContext);
		long count = repository.countAllBooks(bookFilter, securityContext);
		return new PaginationResponse<>(list, bookFilter, count);
	}

	public List<Book> listAllBooks(BookFilter bookFilter,
			SecurityContext securityContext) {
		return repository.listAllBooks(bookFilter, securityContext);
	}

	public void validate(BookFilter bookFilter, SecurityContext securityContext) {
		Set<String> authorIds = bookFilter.getAuthorIds();
		Map<String, Author> authorMap = authorIds.isEmpty()
				? new HashMap<>()
				: repository
						.listByIds(Author.class, authorIds, securityContext)
						.parallelStream()
						.collect(Collectors.toMap(f -> f.getId(), f -> f));
		authorIds.removeAll(authorMap.keySet());
		if (!authorIds.isEmpty()) {
			throw new BadRequestException("No Authors with ids " + authorIds);
		}
		bookFilter.setAuthors(new ArrayList<>(authorMap.values()));
	}

	public void validate(BookCreate bookCreate, SecurityContext securityContext) {
		String authorId = bookCreate.getAuthorId();
		Author author = authorId != null ? getByIdOrNull(authorId,
				Author.class, null, securityContext) : null;
		if (author == null) {
			throw new BadRequestException("No Author with id " + authorId);
		}
		bookCreate.setAuthor(author);
	}
}