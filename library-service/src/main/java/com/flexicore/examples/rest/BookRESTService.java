package com.flexicore.examples.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.example.library.model.Book;
import com.flexicore.examples.request.BookCreate;
import com.flexicore.examples.request.BookFilter;
import com.flexicore.examples.request.BookUpdate;
import com.flexicore.examples.service.BookService;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Asaf on 04/06/2017.
 */
@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/Book")
@Tag(name = "Book")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Extension
@Component
public class BookRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private BookService service;

	@POST
	@Path("createBook")
	@Operation(summary = "createBook", description = "Creates Book")
	public Book createBook(
			@HeaderParam("authenticationKey") String authenticationKey,
			BookCreate bookCreate, @Context SecurityContext securityContext) {
		service.validate(bookCreate, securityContext);
		return service.createBook(bookCreate, securityContext);
	}

	@PUT
	@Operation(summary = "updateBook", description = "Updates Book")
	@Path("updateBook")
	public void updateBook(
			@HeaderParam("authenticationKey") String authenticationKey,
			BookUpdate bookUpdate, @Context SecurityContext securityContext) {
		String bookId = bookUpdate.getId();
		Book book = service.getByIdOrNull(bookId, Book.class, null,
				securityContext);
		if (book == null) {
			throw new BadRequestException("No Book with id " + bookId);
		}
		bookUpdate.setBook(book);
		service.validate(bookUpdate, securityContext);
		service.updateBook(bookUpdate, securityContext);
	}

	@POST
	@Operation(summary = "getAllBooks", description = "Gets All Books Filtered")
	@Path("getAllBooks")
	public PaginationResponse<Book> getAllBooks(
			@HeaderParam("authenticationKey") String authenticationKey,
			BookFilter bookFilter, @Context SecurityContext securityContext) {
		service.validate(bookFilter, securityContext);
		return service.getAllBooks(bookFilter, securityContext);
	}
}
