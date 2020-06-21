package com.flexicore.examples;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.example.library.model.Author;
import com.flexicore.example.library.model.Book;
import com.flexicore.examples.request.AuthorCreate;
import com.flexicore.examples.request.BookCreate;
import com.flexicore.examples.request.BookFilter;
import com.flexicore.examples.request.BookUpdate;
import com.flexicore.init.FlexiCoreApplication;
import com.flexicore.request.AuthenticationRequest;
import com.flexicore.response.AuthenticationResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FlexiCoreApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class BookRESTServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final Logger logger= LoggerFactory.getLogger(BookRESTServiceTest.class);
    private Book book;

    @BeforeAll
    private void init() {
        ResponseEntity<AuthenticationResponse> authenticationResponse = this.restTemplate.postForEntity("/FlexiCore/rest/authenticationNew/login", new AuthenticationRequest().setEmail("admin@flexicore.com").setPassword("admin"), AuthenticationResponse.class);
        String authenticationKey = authenticationResponse.getBody().getAuthenticationKey();
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", authenticationKey);
                    return execution.execute(request, body);
                }));
    }



    @Test
    @Order(1)
    public void testCreateBook() {
        AuthorCreate authorCreate=new AuthorCreate()
                .setFirstName("com")
                .setLastName("wizzdi");
        ResponseEntity<Author> authorResponseEntity = this.restTemplate.postForEntity("/FlexiCore/rest/plugins/Author/createAuthor", authorCreate, Author.class);
        Assertions.assertEquals(200, authorResponseEntity.getStatusCodeValue());
        Author author = authorResponseEntity.getBody();
        Assertions.assertNotNull(author);
        BookCreate bookCreate=new BookCreate()
                .setAuthorId(author.getId())
                .setName("FlexiCore Manual")
                .setDescription("Manual containing info on how to run FlexiCore");

        ResponseEntity<Book> bookResponseEntity = this.restTemplate.postForEntity("/FlexiCore/rest/plugins/Book/createBook", bookCreate, Book.class);
        Assertions.assertEquals(200, bookResponseEntity.getStatusCodeValue());
        book = bookResponseEntity.getBody();
        Assertions.assertNotNull(book);
        validateBook(book,bookCreate);
        logger.info(book+"");


    }

    private void validateBook(Book book, BookCreate bookCreate) {
        Assertions.assertEquals(bookCreate.getAuthorId(),book.getAuthor().getId());
        Assertions.assertEquals(bookCreate.getName(),book.getName());
        Assertions.assertEquals(bookCreate.getDescription(),book.getDescription());


    }

    @Test
    @Order(2)
    public void testListAllBooks() {
        BookFilter request=new BookFilter();
        ParameterizedTypeReference<PaginationResponse<Book>> t=new ParameterizedTypeReference<PaginationResponse<Book>>() {};

        ResponseEntity<PaginationResponse<Book>> booksResponse = this.restTemplate.exchange("/FlexiCore/rest/plugins/Book/getAllBooks", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, booksResponse.getStatusCodeValue());
        PaginationResponse<Book> body = booksResponse.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.getList().stream().map(f->f.getName()).anyMatch(f->f.contains(book.getName())));


    }


    @Test
    @Order(3)
    public void testUpdateBook() {
        BookUpdate bookUpdate=new BookUpdate()
                .setId(book.getId())
                .setAuthorId(book.getAuthor().getId())
                .setDescription(book.getDescription() +" REV 2")
                .setName(book.getName() +" REV 2");

        ResponseEntity<Book> bookResponseEntity = this.restTemplate.exchange("/FlexiCore/rest/plugins/Book/updateBook", HttpMethod.PUT, new HttpEntity<>(bookUpdate), Book.class);
        Assertions.assertEquals(200, bookResponseEntity.getStatusCodeValue());
        book = bookResponseEntity.getBody();
        Assertions.assertNotNull(book);
        validateBook(book,bookUpdate);


    }




}
