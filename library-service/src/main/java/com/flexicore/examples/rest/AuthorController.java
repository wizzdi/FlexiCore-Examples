package com.flexicore.examples.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.example.library.model.Author;
import com.flexicore.example.library.model.Author_;
import com.flexicore.examples.request.AuthorCreate;
import com.flexicore.examples.request.AuthorFilter;
import com.flexicore.examples.request.AuthorUpdate;
import com.flexicore.examples.service.AuthorService;
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
@RequestMapping("/author")
@Extension
@Tag(name = "author")
@OperationsInside
public class AuthorController implements Plugin {

	
	@Autowired
	private AuthorService authorService;

	@PostMapping("createAuthor")
	@Operation(summary = "createAuthor", description = "Creates Author")
	public Author createAuthor(
			@RequestHeader("authenticationKey") String authenticationKey, @RequestBody 
			AuthorCreate authorCreate, @RequestAttribute SecurityContextBase securityContext) {
		authorService.validate(authorCreate, securityContext);
		return authorService.createAuthor(authorCreate, securityContext);
	}

	@Operation(summary = "updateAuthor", description = "Updates Author")
	@PutMapping("updateAuthor")
	public Author updateAuthor(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody 
			AuthorUpdate authorUpdate, @RequestAttribute SecurityContextBase securityContext) {
		String authorId = authorUpdate.getId();
		Author author = authorService.getByIdOrNull(authorId, Author.class, Author_.security,
				securityContext);
		if (author == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Author with id " + authorId);
		}
		authorUpdate.setAuthor(author);
		authorService.validate(authorUpdate, securityContext);
		return authorService.updateAuthor(authorUpdate, securityContext);
	}

	@Operation(summary = "getAllAuthors", description = "Gets All Authors Filtered")
	@PostMapping("getAllAuthors")
	public PaginationResponse<Author> getAllAuthors(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody 
			AuthorFilter authorFilter, @RequestAttribute SecurityContextBase securityContext) {
		authorService.validate(authorFilter, securityContext);
		return authorService.getAllAuthors(authorFilter, securityContext);
	}
}
