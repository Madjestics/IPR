package com.example.brokerstest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesDto {
    private Long userId;
    private List<String> preferredGenres;
}
