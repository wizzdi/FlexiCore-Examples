package com.flexicore.examples.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.ProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.example.library.model.Subscription;
import com.flexicore.examples.request.SubscriptionCreate;
import com.flexicore.examples.request.SubscriptionFilter;
import com.flexicore.examples.request.SubscriptionUpdate;
import com.flexicore.examples.service.SubscriptionService;
import com.flexicore.interfaces.RestServicePlugin;
import com.flexicore.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Created by Asaf on 04/06/2017.
 */
@PluginInfo(version = 1)
@OperationsInside
@ProtectedREST
@Path("plugins/Subscription")
@Tag(name = "Subscription")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Extension
@Component
public class SubscriptionRESTService implements RestServicePlugin {

	@PluginInfo(version = 1)
	@Autowired
	private SubscriptionService service;

	@POST
	@Path("createSubscription")
	@Operation(summary = "createSubscription", description = "Creates Subscription")
	public Subscription createSubscription(
			@HeaderParam("authenticationKey") String authenticationKey,
			SubscriptionCreate subscriptionCreate,
			@Context SecurityContext securityContext) {
		service.validate(subscriptionCreate, securityContext);
		return service.createSubscription(subscriptionCreate, securityContext);
	}

	@PUT
	@Operation(summary = "updateSubscription", description = "Updates Subscription")
	@Path("updateSubscription")
	public void updateSubscription(
			@HeaderParam("authenticationKey") String authenticationKey,
			SubscriptionUpdate subscriptionUpdate,
			@Context SecurityContext securityContext) {
		String subscriptionId = subscriptionUpdate.getId();
		Subscription subscription = service.getByIdOrNull(subscriptionId,
				Subscription.class, null, securityContext);
		if (subscription == null) {
			throw new BadRequestException("No Subscription with id "
					+ subscriptionId);
		}
		subscriptionUpdate.setSubscription(subscription);
		service.validate(subscriptionUpdate, securityContext);
		service.updateSubscription(subscriptionUpdate, securityContext);
	}

	@POST
	@Operation(summary = "getAllSubscriptions", description = "Gets All Subscriptions Filtered")
	@Path("getAllSubscriptions")
	public PaginationResponse<Subscription> getAllSubscriptions(
			@HeaderParam("authenticationKey") String authenticationKey,
			SubscriptionFilter subscriptionFilter,
			@Context SecurityContext securityContext) {
		service.validate(subscriptionFilter, securityContext);
		return service.getAllSubscriptions(subscriptionFilter, securityContext);
	}
}
