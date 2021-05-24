package com.flexicore.examples.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.example.library.model.Book;
import com.flexicore.example.library.model.Book_;
import com.flexicore.examples.request.BookCreate;
import com.flexicore.examples.request.BookFilter;
import com.flexicore.examples.request.BookUpdate;
import com.flexicore.examples.service.BookService;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;



@RestController
@RequestMapping("/book")
@Extension
@Tag(name = "book")
@OperationsInside
public class BookController implements Plugin {

	
	@Autowired
	private BookService service;

	@PostMapping("createBook")
	@Operation(summary = "createBook", description = "Creates Book")
	public Book createBook(
			@RequestHeader("authenticationKey") String authenticationKey, @RequestBody
			BookCreate bookCreate, @RequestAttribute SecurityContextBase securityContext) {
		service.validateCreate(bookCreate, securityContext);
		return service.createBook(bookCreate, securityContext);
	}

	@Operation(summary = "updateBook", description = "Updates Book")
	@PutMapping("updateBook")
	public Book updateBook(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody 
			BookUpdate bookUpdate, @RequestAttribute SecurityContextBase securityContext) {
		String bookId = bookUpdate.getId();
		Book book = service.getByIdOrNull(bookId, Book.class, Book_.security,
				securityContext);
		if (book == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Book with id " + bookId);
		}
		bookUpdate.setBook(book);
		service.validate(bookUpdate, securityContext);
		return service.updateBook(bookUpdate, securityContext);
	}

	@Operation(summary = "getAllBooks", description = "Gets All Books Filtered")
	@PostMapping("getAllBooks")
	public PaginationResponse<Book> getAllBooks(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody 
			BookFilter bookFilter, @RequestAttribute SecurityContextBase securityContext) {
		service.validateCreate(bookFilter, securityContext);
		return service.getAllBooks(bookFilter, securityContext);
	}
}
