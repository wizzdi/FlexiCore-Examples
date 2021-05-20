package com.flexicore.example.library.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.example.person.Person;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Author extends Person {


    @OneToMany(targetEntity = Book.class,mappedBy = "author")
    @JsonIgnore
    private List<Book> books=new ArrayList<>();

    @OneToMany(targetEntity = Book.class,mappedBy = "author")
    @JsonIgnore
    public List<Book> getBooks() {
        return books;
    }

    public <T extends Author> T setBooks(List<Book> books) {
        this.books = books;
        return (T) this;
    }
}
