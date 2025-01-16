package com.example.webfluxtest.service;

import com.example.commontest.dto.MovieEvent;
import com.example.commontest.dto.WatchEvent;

public interface MessageService {
    void sendMovieEvent(MovieEvent movieInfo);

    void sendWatchEvent(WatchEvent watchInfo);
}
