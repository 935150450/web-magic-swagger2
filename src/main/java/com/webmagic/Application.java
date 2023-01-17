package com.webmagic;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args){
        new SpringApplicationBuilder().web(true).sources(com.webmagic.Application.class).run(args);
        System.out.println("http://localhost:8060/swagger-ui.html");
    }

}
