package com.example.brokerstest.entity;

import com.example.commontest.dto.MovieDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
    @Id
    private String id;
    private Long userId;
    private List<String> preferredGenres;
    private List<MovieDto> viewedMovies;
    private List<MovieDto> recommendedMovies;
}
