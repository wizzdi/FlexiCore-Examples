package com.flexicore.examples.service;

import com.flexicore.example.library.model.Author;
import com.flexicore.example.library.model.Author_;
import com.flexicore.example.library.model.Book;
import com.flexicore.examples.data.BookRepository;
import com.flexicore.examples.request.BookCreate;
import com.flexicore.examples.request.BookFilter;
import com.flexicore.examples.request.BookUpdate;
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
public class BookService implements Plugin {


    @Autowired
    private BookRepository repository;
    @Autowired
    private BasicService basicService;

    public Book createBook(BookCreate bookCreate,
                           SecurityContextBase securityContext) {
        Book book = createBookNoMerge(bookCreate, securityContext);
        repository.merge(book);
        return book;
    }

    public Book createBookNoMerge(BookCreate bookCreate,
                                  SecurityContextBase securityContext) {
        Book book = new Book();
        book.setId(UUID.randomUUID().toString());
        updateBookNoMerge(book, bookCreate);
        BaseclassService.createSecurityObjectNoMerge(book, securityContext);
        return book;
    }

    public boolean updateBookNoMerge(Book book, BookCreate bookCreate) {
        boolean update = basicService.updateBasicNoMerge(bookCreate, book);
        if (bookCreate.getAuthor() != null && (book.getAuthor() == null || !bookCreate.getAuthor().getId().equals(book.getAuthor().getId()))) {
            book.setAuthor(bookCreate.getAuthor());
            update = true;
        }

        return update;
    }

    public Book updateBook(BookUpdate bookUpdate,
                           SecurityContextBase securityContext) {
        Book book = bookUpdate.getBook();
        if (updateBookNoMerge(book, bookUpdate)) {
            repository.merge(book);
        }
        return book;
    }

    public PaginationResponse<Book> getAllBooks(BookFilter bookFilter,
                                                SecurityContextBase securityContext) {
        List<Book> list = listAllBooks(bookFilter, securityContext);
        long count = repository.countAllBooks(bookFilter, securityContext);
        return new PaginationResponse<>(list, bookFilter, count);
    }

    public List<Book> listAllBooks(BookFilter bookFilter,
                                   SecurityContextBase securityContext) {
        return repository.listAllBooks(bookFilter, securityContext);
    }

    public void validateCreate(BookFilter bookFilter, SecurityContextBase securityContext) {
        Set<String> authorIds = bookFilter.getAuthorIds();
        Map<String, Author> authorMap = authorIds.isEmpty()
                ? new HashMap<>()
                : repository
                .listByIds(Author.class, authorIds, Author_.security, securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
        authorIds.removeAll(authorMap.keySet());
        if (!authorIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Authors with ids " + authorIds);
        }
        bookFilter.setAuthors(new ArrayList<>(authorMap.values()));
    }

    public void validateCreate(BookCreate bookCreate, SecurityContextBase securityContext) {
        validate(bookCreate, securityContext);
	    if(bookCreate.getAuthor()==null){
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no author provided");
        }
    }

    public void validate(BookCreate bookCreate, SecurityContextBase securityContext) {
        String authorId = bookCreate.getAuthorId();
        Author author = authorId != null ? repository.getByIdOrNull(authorId,
                Author.class, Author_.security, securityContext) : null;
        if (author == null && authorId != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Author with id " + authorId);
        }
        bookCreate.setAuthor(author);
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