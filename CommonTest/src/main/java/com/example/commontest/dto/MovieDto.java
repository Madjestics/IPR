package com.example.commontest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class MovieDto {
    private Long id;
    private String title;
    private Integer year;
    private LocalTime duration;
    private Double rating;
    private String genre;
    private Long directorId;
}
