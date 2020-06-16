package com.flexicore.examples.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.example.library.model.Subscription;

public class SubscriptionUpdate extends SubscriptionCreate {

	private String id;
	@JsonIgnore
	private Subscription subscription;

	public String getId() {
		return id;
	}

	public <T extends SubscriptionUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public Subscription getSubscription() {
		return subscription;
	}

	public <T extends SubscriptionUpdate> T setSubscription(
			Subscription subscription) {
		this.subscription = subscription;
		return (T) this;
	}
}
