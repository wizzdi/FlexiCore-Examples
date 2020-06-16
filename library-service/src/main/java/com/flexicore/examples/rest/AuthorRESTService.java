package com.flexicore.examples.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.example.library.model.Author;
import com.flexicore.examples.request.AuthorCreate;
import com.flexicore.examples.request.AuthorFilter;
import com.flexicore.examples.request.AuthorUpdate;
import com.flexicore.examples.service.AuthorService;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Path("plugins/Author")
@Tag(name = "Author")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Extension
@Component
public class AuthorRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private AuthorService service;

	@POST
	@Path("createAuthor")
	@Operation(summary = "createAuthor", description = "Creates Author")
	public Author createAuthor(
			@HeaderParam("authenticationKey") String authenticationKey,
			AuthorCreate authorCreate, @Context SecurityContext securityContext) {
		service.validate(authorCreate, securityContext);
		return service.createAuthor(authorCreate, securityContext);
	}

	@PUT
	@Operation(summary = "updateAuthor", description = "Updates Author")
	@Path("updateAuthor")
	public void updateAuthor(
			@HeaderParam("authenticationKey") String authenticationKey,
			AuthorUpdate authorUpdate, @Context SecurityContext securityContext) {
		String authorId = authorUpdate.getId();
		Author author = service.getByIdOrNull(authorId, Author.class, null,
				securityContext);
		if (author == null) {
			throw new BadRequestException("No Author with id " + authorId);
		}
		authorUpdate.setAuthor(author);
		service.validate(authorUpdate, securityContext);
		service.updateAuthor(authorUpdate, securityContext);
	}

	@POST
	@Operation(summary = "getAllAuthors", description = "Gets All Authors Filtered")
	@Path("getAllAuthors")
	public PaginationResponse<Author> getAllAuthors(
			@HeaderParam("authenticationKey") String authenticationKey,
			AuthorFilter authorFilter, @Context SecurityContext securityContext) {
		service.validate(authorFilter, securityContext);
		return service.getAllAuthors(authorFilter, securityContext);
	}
}
