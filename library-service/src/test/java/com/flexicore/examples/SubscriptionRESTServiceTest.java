package com.flexicore.examples;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.example.library.model.Author;
import com.flexicore.example.library.model.Book;
import com.flexicore.example.library.model.Subscription;
import com.flexicore.example.person.Person;
import com.flexicore.examples.request.*;
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

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collections;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FlexiCoreApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class SubscriptionRESTServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final Logger logger= LoggerFactory.getLogger(SubscriptionRESTServiceTest.class);
    private Subscription subscription;

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
    public void testCreateSubscription() {
        PersonCreate personCreate=new PersonCreate()
                .setFirstName("John")
                .setLastName("Doe");
        ResponseEntity<Person> personResponseEntity = this.restTemplate.postForEntity("/FlexiCore/rest/plugins/Person/createPerson", personCreate, Person.class);
        Assertions.assertEquals(200, personResponseEntity.getStatusCodeValue());
        Person subscriber = personResponseEntity.getBody();
        Assertions.assertNotNull(subscriber);

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
        Book book = bookResponseEntity.getBody();
        Assertions.assertNotNull(book);

        Assertions.assertNotNull(author);
        SubscriptionCreate subscriptionCreate=new SubscriptionCreate()
                .setBookId(book.getId())
                .setPersonId(subscriber.getId())
                .setStartTime(OffsetDateTime.now());

        ResponseEntity<Subscription> subscriptionResponseEntity = this.restTemplate.postForEntity("/FlexiCore/rest/plugins/Subscription/createSubscription", subscriptionCreate, Subscription.class);
        Assertions.assertEquals(200, subscriptionResponseEntity.getStatusCodeValue());
        subscription = subscriptionResponseEntity.getBody();
        Assertions.assertNotNull(subscription);
        validateSubscription(subscription,subscriptionCreate);
        logger.info(subscription+"");


    }

    private void validateSubscription(Subscription subscription, SubscriptionCreate subscriptionCreate) {
        if(subscriptionCreate.getBookId()!=null){
            Assertions.assertEquals(subscriptionCreate.getBookId(),subscription.getBook().getId());
        }
        if(subscriptionCreate.getPersonId()!=null){
            Assertions.assertEquals(subscriptionCreate.getPersonId(),subscription.getPerson().getId());
        }
        if(subscriptionCreate.getStartTime()!=null){
            Assertions.assertEquals(subscriptionCreate.getStartTime().atZoneSameInstant(ZoneId.systemDefault()),subscription.getStartTime().atZoneSameInstant(ZoneId.systemDefault()));
        }
        if(subscriptionCreate.getEndTime()!=null){
            Assertions.assertEquals(subscriptionCreate.getEndTime().atZoneSameInstant(ZoneId.systemDefault()),subscription.getEndTime().atZoneSameInstant(ZoneId.systemDefault()));
        }


    }

    @Test
    @Order(2)
    public void testListAllSubscriptions() {
        SubscriptionFilter request=new SubscriptionFilter();
        ParameterizedTypeReference<PaginationResponse<Subscription>> t=new ParameterizedTypeReference<PaginationResponse<Subscription>>() {};

        ResponseEntity<PaginationResponse<Subscription>> subscriptionsResponse = this.restTemplate.exchange("/FlexiCore/rest/plugins/Subscription/getAllSubscriptions", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, subscriptionsResponse.getStatusCodeValue());
        PaginationResponse<Subscription> body = subscriptionsResponse.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.getList().stream().map(f->f.getName()).anyMatch(f->f.contains(subscription.getName())));


    }


    @Test
    @Order(3)
    public void testUpdateSubscription() {
        SubscriptionUpdate subscriptionUpdate=new SubscriptionUpdate()
               .setId(subscription.getId())
                .setEndTime(OffsetDateTime.now());

        ResponseEntity<Subscription> subscriptionResponseEntity = this.restTemplate.exchange("/FlexiCore/rest/plugins/Subscription/updateSubscription", HttpMethod.PUT, new HttpEntity<>(subscriptionUpdate), Subscription.class);
        Assertions.assertEquals(200, subscriptionResponseEntity.getStatusCodeValue());
        subscription = subscriptionResponseEntity.getBody();
        Assertions.assertNotNull(subscription);
        validateSubscription(subscription,subscriptionUpdate);


    }




}
