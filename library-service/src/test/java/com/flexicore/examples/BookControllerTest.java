package com.flexicore.examples;


import com.flexicore.example.library.model.Author;
import com.flexicore.example.library.model.Book;
import com.flexicore.examples.app.App;
import com.flexicore.examples.request.BookCreate;
import com.flexicore.examples.request.BookFilter;
import com.flexicore.examples.request.BookUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {App.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class BookControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Author author;


    private Book book;


    @BeforeAll
    private void init() {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", "fake");
                    return execution.execute(request, body);
                }));

    }

    @Test
    @Order(1)
    public void createBook() throws InterruptedException {
        BookCreate request = new BookCreate()
                .setAuthorId(author.getId())
                .setName("test book");

        ParameterizedTypeReference<Book> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<Book> response = this.restTemplate.exchange("/book/createBook", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        book = response.getBody();
        Assertions.assertNotNull(book);
        validateBook(request);


    }

    private void validateBook(BookCreate request) {
        if(request.getName()!=null){
            Assertions.assertEquals(request.getName(), book.getName());
        }
        if(request.getAuthorId()!=null){
            Assertions.assertNotNull(book.getAuthor());
            Assertions.assertEquals(request.getAuthorId(), book.getAuthor().getId());
        }
    }

    @Test
    @Order(2)
    public void testGetAllBooks() throws InterruptedException {
        BookFilter request = new BookFilter();
        ParameterizedTypeReference<PaginationResponse<Book>> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<PaginationResponse<Book>> response = this.restTemplate.exchange("/book/getAllBooks", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        PaginationResponse<Book> body = response.getBody();
        Assertions.assertNotNull(body);
        List<Book> books = body.getList();
        Assertions.assertTrue(books.stream().anyMatch(f->f.getId().equals(book.getId())));

    }

    @Test
    @Order(3)
    public void testUpdateBook() throws InterruptedException {
        BookUpdate request = new BookUpdate()
                .setId(book.getId())
                .setName("new name");
        ParameterizedTypeReference<Book> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<Book> response = this.restTemplate.exchange("/book/updateBook", HttpMethod.PUT, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        book= response.getBody();
        Assertions.assertNotNull(book);
        validateBook(request);

    }


}
