package com.zhao.test;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.zhao.mapper")
@EnableScheduling
public class UserApplication extends SpringBootServletInitializer {


    //
    /**/
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(UserApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}