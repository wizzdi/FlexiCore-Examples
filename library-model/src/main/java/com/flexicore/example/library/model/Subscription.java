package com.flexicore.example.library.model;

import com.flexicore.example.person.Person;
import com.flexicore.model.Baseclass;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.OffsetDateTime;

@Entity
public class Subscription extends Baseclass {
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;

    @ManyToOne(targetEntity = Book.class)
    private Book book;
    @ManyToOne(targetEntity = Person.class)
    private Person person;

    @ManyToOne(targetEntity = Book.class)
    public Book getBook() {
        return book;
    }

    public <T extends Subscription> T setBook(Book book) {
        this.book = book;
        return (T) this;
    }

    @ManyToOne(targetEntity = Person.class)
    public Person getPerson() {
        return person;
    }

    public <T extends Subscription> T setPerson(Person person) {
        this.person = person;
        return (T) this;
    }

    public OffsetDateTime getStartTime() {
        return startTime;
    }

    public <T extends Subscription> T setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
        return (T) this;
    }

    public OffsetDateTime getEndTime() {
        return endTime;
    }

    public <T extends Subscription> T setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
        return (T) this;
    }
}
