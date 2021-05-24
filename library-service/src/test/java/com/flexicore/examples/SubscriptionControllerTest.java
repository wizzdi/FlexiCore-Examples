package com.flexicore.examples;


import com.flexicore.example.library.model.Book;
import com.flexicore.example.library.model.Subscription;
import com.flexicore.example.person.Person;
import com.flexicore.examples.app.App;
import com.flexicore.examples.request.SubscriptionCreate;
import com.flexicore.examples.request.SubscriptionFilter;
import com.flexicore.examples.request.SubscriptionUpdate;
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

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {App.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class SubscriptionControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Person person;
    @Autowired
    private Book book;

    private Subscription subscription;


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
    public void createSubscription() throws InterruptedException {
        SubscriptionCreate request = new SubscriptionCreate()
                .setBookId(book.getId())
                .setPersonId(person.getId())
                .setStartTime(OffsetDateTime.now())
                .setEndTime(OffsetDateTime.now().plusDays(3))
                .setName("Subscription")
                .setDescription("Subscription made by XXX");

        ParameterizedTypeReference<Subscription> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<Subscription> response = this.restTemplate.exchange("/subscription/createSubscription", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        subscription = response.getBody();
        Assertions.assertNotNull(subscription);
        validateSubscription(request);


    }

    private void validateSubscription(SubscriptionCreate request) {
        if(request.getName()!=null){
            Assertions.assertEquals(request.getName(), subscription.getName());
        }
        if(request.getStartTime()!=null){
            Assertions.assertEquals(request.getStartTime().toInstant(), subscription.getStartTime().toInstant());
        }
        if(request.getEndTime()!=null){
            Assertions.assertEquals(request.getEndTime().toInstant(), subscription.getEndTime().toInstant());
        }
        if(request.getBookId()!=null){
            Assertions.assertNotNull(subscription.getBook());
            Assertions.assertEquals(request.getBookId(), subscription.getBook().getId());
        }
        if(request.getPersonId()!=null){
            Assertions.assertNotNull(subscription.getPerson());
            Assertions.assertEquals(request.getPersonId(), subscription.getPerson().getId());
        }
    }

    @Test
    @Order(2)
    public void testGetAllSubscriptions() throws InterruptedException {
        SubscriptionFilter request = new SubscriptionFilter();
        ParameterizedTypeReference<PaginationResponse<Subscription>> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<PaginationResponse<Subscription>> response = this.restTemplate.exchange("/subscription/getAllSubscriptions", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        PaginationResponse<Subscription> body = response.getBody();
        Assertions.assertNotNull(body);
        List<Subscription> subscriptions = body.getList();
        Assertions.assertTrue(subscriptions.stream().anyMatch(f->f.getId().equals(subscription.getId())));

    }

    @Test
    @Order(3)
    public void testUpdateSubscription() throws InterruptedException {
        SubscriptionUpdate request = new SubscriptionUpdate()
                .setId(subscription.getId())
                .setName("new name");
        ParameterizedTypeReference<Subscription> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<Subscription> response = this.restTemplate.exchange("/subscription/updateSubscription", HttpMethod.PUT, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        subscription= response.getBody();
        Assertions.assertNotNull(subscription);
        validateSubscription(request);

    }


}
