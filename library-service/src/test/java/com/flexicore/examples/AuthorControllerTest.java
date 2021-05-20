package com.flexicore.examples;


import com.flexicore.example.library.model.Author;
import com.flexicore.examples.app.App;
import com.flexicore.examples.request.AuthorCreate;
import com.flexicore.examples.request.AuthorFilter;
import com.flexicore.examples.request.AuthorUpdate;
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
public class AuthorControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;



    private Author author;


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
    public void createAuthor() throws InterruptedException {
        AuthorCreate request = new AuthorCreate()
                .setFirstName("test")
                .setLastName("test author")
                .setName("test author");

        ParameterizedTypeReference<Author> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<Author> response = this.restTemplate.exchange("/author/createAuthor", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        author = response.getBody();
        Assertions.assertNotNull(author);
        validateAuthor(request);


    }

    private void validateAuthor(AuthorCreate request) {
        if(request.getName()!=null){
            Assertions.assertEquals(request.getName(), author.getName());
        }
        if(request.getFirstName()!=null){
            Assertions.assertEquals(request.getFirstName(), author.getFirstName());
        }
        if(request.getLastName()!=null){
            Assertions.assertEquals(request.getLastName(), author.getLastName());
        }
    }

    @Test
    @Order(2)
    public void testGetAllAuthors() throws InterruptedException {
        AuthorFilter request = new AuthorFilter();
        ParameterizedTypeReference<PaginationResponse<Author>> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<PaginationResponse<Author>> response = this.restTemplate.exchange("/author/getAllAuthors", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        PaginationResponse<Author> body = response.getBody();
        Assertions.assertNotNull(body);
        List<Author> authors = body.getList();
        Assertions.assertTrue(authors.stream().anyMatch(f->f.getId().equals(author.getId())));

    }

    @Test
    @Order(3)
    public void testUpdateAuthor() throws InterruptedException {
        AuthorUpdate request = new AuthorUpdate()
                .setId(author.getId())
                .setName("new name");
        ParameterizedTypeReference<Author> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<Author> response = this.restTemplate.exchange("/author/updateAuthor", HttpMethod.PUT, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        author= response.getBody();
        Assertions.assertNotNull(author);
        validateAuthor(request);

    }


}
