package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;

@Getter
@Setter
@Data
public class Film {
    Long id;
    String name;
    String description;
    Instant releaseDate;
    Duration duration;
}
