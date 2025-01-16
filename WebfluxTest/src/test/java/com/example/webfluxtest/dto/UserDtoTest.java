package com.example.webfluxtest.dto;

import com.example.webfluxtest.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDtoTest {
    private Long id;
    private String username;
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private String password;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
