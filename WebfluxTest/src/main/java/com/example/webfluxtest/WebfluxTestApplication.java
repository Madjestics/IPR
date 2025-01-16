package com.example.webfluxtest;

import com.example.webfluxtest.mapper.UserMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WebfluxTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebfluxTestApplication.class, args);
    }

    @Bean
    public UserMapper userMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

}
