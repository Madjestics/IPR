package com.example.webfluxtest.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "director", schema = "public")
public class Director {
    @Id
    private Long id;
    private String fio;
}
