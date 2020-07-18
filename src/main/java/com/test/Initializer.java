package com.test;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;
import java.util.*;

@SpringBootApplication
@Configuration
@EnableCaching
public class Initializer{
    
    public static void main(String[] args){
        SpringApplication.run(Initializer.class);
    }
    
}