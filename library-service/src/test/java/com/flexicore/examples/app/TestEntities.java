package com.flexicore.examples.app;

import com.flexicore.example.library.model.Author;
import com.flexicore.example.library.model.Book;
import com.flexicore.example.person.Person;
import com.flexicore.examples.request.AuthorCreate;
import com.flexicore.examples.request.BookCreate;
import com.flexicore.examples.request.PersonCreate;
import com.flexicore.examples.service.AuthorService;
import com.flexicore.examples.service.BookService;
import com.flexicore.examples.service.PersonService;
import com.flexicore.security.SecurityContextBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestEntities {


    @Autowired
    private AuthorService authorService;
    @Autowired
    private PersonService personService;
    @Autowired
    private BookService bookService;

    @Autowired
    private SecurityContextBase adminSecurityContext;

    @Bean
    public Author author(){
        return authorService.createAuthor(new AuthorCreate().setFirstName("test").setFirstName("author").setName("test"),adminSecurityContext);
    }

    @Bean
    public Person person(){
        return personService.createPerson(new PersonCreate().setFirstName("test").setFirstName("author").setName("test"),adminSecurityContext);
    }

    @Bean
    public Book book(Author author){
        return bookService.createBook(new BookCreate().setAuthor(author).setName("test"),adminSecurityContext);
    }
}
