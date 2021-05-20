package com.flexicore.examples;


import com.flexicore.example.person.Person;
import com.flexicore.examples.app.App;
import com.flexicore.examples.request.PersonCreate;
import com.flexicore.examples.request.PersonFilter;
import com.flexicore.examples.request.PersonUpdate;
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
public class PersonControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;



    private Person person;


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
    public void createPerson() throws InterruptedException {
        PersonCreate request = new PersonCreate()
                .setFirstName("test")
                .setLastName("test person")
                .setName("test person");

        ParameterizedTypeReference<Person> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<Person> response = this.restTemplate.exchange("/person/createPerson", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        person = response.getBody();
        Assertions.assertNotNull(person);
        validatePerson(request);


    }

    private void validatePerson(PersonCreate request) {
        if(request.getName()!=null){
            Assertions.assertEquals(request.getName(), person.getName());
        }
        if(request.getFirstName()!=null){
            Assertions.assertEquals(request.getFirstName(), person.getFirstName());
        }
        if(request.getLastName()!=null){
            Assertions.assertEquals(request.getLastName(), person.getLastName());
        }
    }

    @Test
    @Order(2)
    public void testGetAllPersons() throws InterruptedException {
        PersonFilter request = new PersonFilter();
        ParameterizedTypeReference<PaginationResponse<Person>> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<PaginationResponse<Person>> response = this.restTemplate.exchange("/person/getAllPersons", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        PaginationResponse<Person> body = response.getBody();
        Assertions.assertNotNull(body);
        List<Person> persons = body.getList();
        Assertions.assertTrue(persons.stream().anyMatch(f->f.getId().equals(person.getId())));

    }

    @Test
    @Order(3)
    public void testUpdatePerson() throws InterruptedException {
        PersonUpdate request = new PersonUpdate()
                .setId(person.getId())
                .setName("new name");
        ParameterizedTypeReference<Person> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<Person> response = this.restTemplate.exchange("/person/updatePerson", HttpMethod.PUT, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        person= response.getBody();
        Assertions.assertNotNull(person);
        validatePerson(request);

    }


}
