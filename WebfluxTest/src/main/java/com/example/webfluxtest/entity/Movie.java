package com.example.webfluxtest.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalTime;

@Data
@Table(name = "movie", schema = "public")
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    @Id
    private Long id;
    private String title;
    private Integer year;
    private LocalTime duration;
    private Double rating;
    private String genre;
    @Transient
    private Director director;
    @Column("director")
    private Long directorId;
    @Column("filePath")
    private String filePath;

    public Movie(Long id, String title, Integer year, LocalTime duration, Double rating, String genre, Long directorId) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.duration = duration;
        this.genre = genre;
        this.rating = rating;
        this.directorId = directorId;
    }
}
