package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable("id") Long id) {
        return filmService.findFilmById(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        return filmService.updateFilm(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addingLikeMovie(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        filmService.addingLikeMovie(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFromMovie(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        filmService.removeLikeFromMovie(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> outputOfPopularMovies(@RequestParam(required = false, defaultValue = "10") Integer count) {
        return filmService.outputOfPopularMovies(count);
    }
}
