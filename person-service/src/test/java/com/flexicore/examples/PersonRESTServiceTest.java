package com.flexicore.examples;

import com.flexicore.data.jsoncontainers.PaginationResponse;
import com.flexicore.example.person.Person;
import com.flexicore.examples.request.PersonCreate;
import com.flexicore.examples.request.PersonFilter;
import com.flexicore.examples.request.PersonUpdate;
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

public class PersonRESTServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final Logger logger= LoggerFactory.getLogger(PersonRESTServiceTest.class);
    private Person person;

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
    public void testCreatePerson() {
        PersonCreate personCreate=new PersonCreate()
                .setFirstName("John")
                .setLastName("Doe");

        ResponseEntity<Person> personResponseEntity = this.restTemplate.postForEntity("/FlexiCore/rest/plugins/Person/createPerson", personCreate, Person.class);
        Assertions.assertEquals(200, personResponseEntity.getStatusCodeValue());
        person = personResponseEntity.getBody();
        Assertions.assertNotNull(person);
        validatePerson(person,personCreate);
        logger.info(person+"");


    }

    private void validatePerson(Person person, PersonCreate personCreate) {
        Assertions.assertEquals(personCreate.getFirstName(),person.getFirstName());
        Assertions.assertEquals(personCreate.getLastName(),person.getLastName());


    }

    @Test
    @Order(2)
    public void testListAllPersons() {
        PersonFilter request=new PersonFilter();
        ParameterizedTypeReference<PaginationResponse<Person>> t=new ParameterizedTypeReference<PaginationResponse<Person>>() {};

        ResponseEntity<PaginationResponse<Person>> personsResponse = this.restTemplate.exchange("/FlexiCore/rest/plugins/Person/getAllPersons", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, personsResponse.getStatusCodeValue());
        PaginationResponse<Person> body = personsResponse.getBody();
        Assertions.assertNotNull(body);
        Assertions.assertTrue(body.getList().stream().map(f->f.getName()).anyMatch(f->f.contains(person.getName())));


    }


    @Test
    @Order(3)
    public void testUpdatePerson() {
        PersonUpdate personUpdate=new PersonUpdate()
                .setId(person.getId())
                .setFirstName(person.getName() +" Updated")
                .setLastName(person.getLastName() +" Updated");

        ResponseEntity<Person> personResponseEntity = this.restTemplate.exchange("/FlexiCore/rest/plugins/Person/updatePerson", HttpMethod.PUT, new HttpEntity<>(personUpdate), Person.class);
        Assertions.assertEquals(200, personResponseEntity.getStatusCodeValue());
        person = personResponseEntity.getBody();
        Assertions.assertNotNull(person);
        validatePerson(person,personUpdate);


    }




}
