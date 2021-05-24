package com.flexicore.examples.app;

import com.flexicore.example.library.model.Author;
import com.flexicore.example.person.Person;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.Clazz;
import com.flexicore.model.security.SecurityPolicy;
import com.wizzdi.flexicore.boot.jpa.service.EntitiesHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
public class EntitiesConfig {

	@Bean
	public EntitiesHolder entitiesHolder(){
		return new EntitiesHolder(new HashSet<>(Arrays.asList(Baseclass.class, Clazz.class, Basic.class, SecurityPolicy.class, Person.class, Author.class)));
	}
}
