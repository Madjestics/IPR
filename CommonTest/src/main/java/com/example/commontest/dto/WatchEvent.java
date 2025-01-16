package com.example.commontest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WatchEvent {
    private MovieDto movie;
    private Long userId;
}