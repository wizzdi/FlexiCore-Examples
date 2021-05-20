package com.flexicore.examples.rest;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.example.library.model.Subscription;
import com.flexicore.example.library.model.Subscription_;
import com.flexicore.examples.request.SubscriptionCreate;
import com.flexicore.examples.request.SubscriptionFilter;
import com.flexicore.examples.request.SubscriptionUpdate;
import com.flexicore.examples.service.SubscriptionService;
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
@RequestMapping("/subscription")
@Extension
@Tag(name = "subscription")
@OperationsInside
public class SubscriptionController implements Plugin {

	
	@Autowired
	private SubscriptionService service;

	@PostMapping("createSubscription")
	@Operation(summary = "createSubscription", description = "Creates Subscription")
	public Subscription createSubscription(
			@RequestHeader("authenticationKey") String authenticationKey, @RequestBody
			SubscriptionCreate subscriptionCreate,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(subscriptionCreate, securityContext);
		return service.createSubscription(subscriptionCreate, securityContext);
	}

	@Operation(summary = "updateSubscription", description = "Updates Subscription")
	@PutMapping("updateSubscription")
	public Subscription updateSubscription(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody 
			SubscriptionUpdate subscriptionUpdate,
			@RequestAttribute SecurityContextBase securityContext) {
		String subscriptionId = subscriptionUpdate.getId();
		Subscription subscription = service.getByIdOrNull(subscriptionId,
				Subscription.class, Subscription_.security, securityContext);
		if (subscription == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Subscription with id "
					+ subscriptionId);
		}
		subscriptionUpdate.setSubscription(subscription);
		service.validate(subscriptionUpdate, securityContext);
		return service.updateSubscription(subscriptionUpdate, securityContext);
	}

	@Operation(summary = "getAllSubscriptions", description = "Gets All Subscriptions Filtered")
	@PostMapping("getAllSubscriptions")
	public PaginationResponse<Subscription> getAllSubscriptions(
			@RequestHeader("authenticationKey") String authenticationKey,@RequestBody 
			SubscriptionFilter subscriptionFilter,
			@RequestAttribute SecurityContextBase securityContext) {
		service.validate(subscriptionFilter, securityContext);
		return service.getAllSubscriptions(subscriptionFilter, securityContext);
	}
}
