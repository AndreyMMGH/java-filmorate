package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@RequestBody Film film) {
        return ;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        return ;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return ;
    }

    private long getNextId() {
        return ;
    }
}
