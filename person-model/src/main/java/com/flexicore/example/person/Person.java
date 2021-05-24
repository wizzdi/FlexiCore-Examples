package com.flexicore.example.person;

import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;

@Entity
public class Person extends SecuredBasic {

    private String firstName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public <T extends Person> T setFirstName(String firstName) {
        this.firstName = firstName;
        return (T) this;
    }

    public String getLastName() {
        return lastName;
    }

    public <T extends Person> T setLastName(String lastName) {
        this.lastName = lastName;
        return (T) this;
    }


}
