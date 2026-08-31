package com.syncria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class SyncriaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SyncriaApplication.class, args);
    }
}
