package com.example;

import com.flexicore.interfaces.ServicePlugin;
import org.pf4j.Extension;
import org.springframework.stereotype.Component;

@Component
@Extension
public class HelloWorldService implements ServicePlugin {

    public String hello() {
        return "Hello World!";
    }

}