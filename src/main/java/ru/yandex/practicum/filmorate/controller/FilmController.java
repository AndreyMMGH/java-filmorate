package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @GetMapping
    public List<Film> findAll() {
        log.info("Вывод всех фильмов");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable("id") Long id) {
        log.info("Получение фильма по его Id {}", id);
        return filmService.findFilmById(id);
    }

   @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Создание фильма: {}", film);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Обновление фильма: {}", newFilm);
        return filmService.updateFilm(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addingLikeMovie(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Добавление лайка к фильму с id {} пользователем с id: {}", id, userId);
        filmService.addingLikeMovie(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLikeFromMovie(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        log.info("Удаление лайка у фильма с id {} пользователя с id: {}", id, userId);
        filmService.removeLikeFromMovie(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> outputOfPopularMovies(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("Получение рейтинга фильмов");
        return filmService.outputOfPopularMovies(count);
    }
}
