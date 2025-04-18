package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
}
