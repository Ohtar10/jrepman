package org.jrepman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * JRepman.
 *
 * Main Spring Boot Application Class of this service.
 */
@SpringBootApplication
public class JRepman {

    public static void main(String[] args){
        //Simply run the application
        SpringApplication.run(JRepman.class, args);
    }
}
