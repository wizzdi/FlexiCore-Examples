package com.example.rest;

import com.example.HelloWorldService;
import com.flexicore.annotations.OperationsInside;
import com.flexicore.annotations.UnProtectedREST;
import com.flexicore.annotations.plugins.PluginInfo;
import com.flexicore.interfaces.RestServicePlugin;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@OperationsInside
@UnProtectedREST
@Path("plugins/helloWorld")
@Tag(name = "HelloWorld")
@Component
@Extension
@Primary
public class HelloWorldRESTService implements RestServicePlugin {

    @Autowired
    private HelloWorldService service;

    @GET
    public String hello() {
        return service.hello();
    }
}