package com.flexicore.example.library.model;

import com.flexicore.example.person.Person;
import com.flexicore.model.Baseclass;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Book extends Baseclass {
    @ManyToOne(targetEntity = Author.class)
    private Author author;

    @ManyToOne(targetEntity = Author.class)
    public Author getAuthor() {
        return author;
    }

    public <T extends Book> T setAuthor(Author author) {
        this.author = author;
        return (T) this;
    }
}
