package com.flexicore.examples.request;

import com.wizzdi.flexicore.security.request.BasicCreate;

public class PersonCreate extends BasicCreate {
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
