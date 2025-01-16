package com.example.commontest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MovieEvent {
    private Action action;
    private MovieDto movie;
}
