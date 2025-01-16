package com.example.brokerstest.mapper;

import com.example.brokerstest.dto.UserPreferencesDto;
import com.example.brokerstest.entity.UserPreferences;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class UserPreferencesMapper {
    public UserPreferencesDto map(UserPreferences preferences) {
        return new UserPreferencesDto(preferences.getUserId(), preferences.getPreferredGenres());
    }

    public UserPreferences map(UserPreferencesDto preferencesDto) {
        UserPreferences preferences = new UserPreferences();
        preferences.setUserId(preferencesDto.getUserId());
        preferences.setPreferredGenres(preferencesDto.getPreferredGenres());
        preferences.setRecommendedMovies(Collections.emptyList());
        preferences.setViewedMovies(Collections.emptyList());
        return preferences;
    }
}
