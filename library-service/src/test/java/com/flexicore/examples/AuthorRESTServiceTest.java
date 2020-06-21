package com.flexicore.examples;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.example.library.model.Author;
import com.flexicore.example.library.model.Author;
import com.flexicore.examples.request.AuthorCreate;
import com.flexicore.examples.request.AuthorCreate;
import com.flexicore.examples.request.AuthorFilter;
import com.flexicore.examples.request.AuthorUpdate;
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

public class AuthorRESTServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final Logger logger= LoggerFactory.getLogger(AuthorRESTServiceTest.class);
    private Author author;

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
    public void testCreateAuthor() {
        AuthorCreate authorCreate=new AuthorCreate()
                .setFirstName("com")
                .setLastName("wizzdi");
        ResponseEntity<Author> authorResponseEntity = this.restTemplate.postForEntity("/FlexiCore/rest/plugins/Author/createAuthor", authorCreate, Author.class);
        Assertions.assertEquals(200, authorResponseEntity.getStatusCodeValue());
        author = authorResponseEntity.getBody();
        Assertions.assertNotNull(author);
        validateAuthor(author,authorCreate);
        logger.info(author+"");


    }

    private void validateAuthor(Author author, AuthorCreate authorCreate) {
        Assertions.assertEquals(authorCreate.getFirstName(),author.getFirstName());
        Assertions.assertEquals(authorCreate.getLastName(),author.getLastName());


    }

    @Test
    @Order(2)
    public void testListAllAuthors() {
        AuthorFilter request=new AuthorFilter();
        ParameterizedTypeReference<PaginationResponse<Author>> t=new ParameterizedTypeReference<PaginationResponse<Author>>() {};

        ResponseEntity<PaginationResponse<Author>> authorsResponse = this.restTemplate.exchange("/FlexiCore/rest/plugins/Author/getAllAuthors", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, authorsResponse.getStatusCodeValue());
        PaginationResponse<Author> body = authorsResponse.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.getList().stream().map(f->f.getName()).anyMatch(f->f.contains(author.getName())));


    }


    @Test
    @Order(3)
    public void testUpdateAuthor() {
        AuthorUpdate authorUpdate=new AuthorUpdate()
                .setId(author.getId())
                .setFirstName(author.getFirstName() +" Update")
                .setLastName(author.getLastName() +" Update");

        ResponseEntity<Author> authorResponseEntity = this.restTemplate.exchange("/FlexiCore/rest/plugins/Author/updateAuthor", HttpMethod.PUT, new HttpEntity<>(authorUpdate), Author.class);
        Assertions.assertEquals(200, authorResponseEntity.getStatusCodeValue());
        author = authorResponseEntity.getBody();
        Assertions.assertNotNull(author);
        validateAuthor(author,authorUpdate);


    }




}
