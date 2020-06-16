package com.flexicore.examples.request;

public class PersonCreate {
	private String firstName;
	private String lastName;

	public String getFirstName() {
		return firstName;
	}

	public <T extends PersonCreate> T setFirstName(String firstName) {
		this.firstName = firstName;
		return (T) this;
	}

	public String getLastName() {
		return lastName;
	}

	public <T extends PersonCreate> T setLastName(String lastName) {
		this.lastName = lastName;
		return (T) this;
	}
}
