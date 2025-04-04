package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@RequestBody Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug("Фильм {} добавлен!", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {
        log.debug("Проверка на заполнение поля id {} по условию", newFilm.getId());
        if (newFilm.getId() == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            log.debug("Фильмы из хранилища: {}", newFilm);
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());

            log.debug("Фильм {} обновлен", oldFilm);
            return oldFilm;
        }
        log.error("Фильм с данным id - {} не найден", newFilm.getId());
        throw new NotFoundException("Фильм с данным id - " + newFilm.getId() + " не найден");
    }

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    private void validateFilm(Film film) {
        log.debug("Проверка на заполнение поля наименование {} по условию", film.getName());
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        log.debug("Проверка на заполнение поля описание {} по условию", film.getDescription());
        if (film.getDescription() == null || film.getDescription().length() >= 200) {
            log.error("Максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        log.debug("Проверка на заполнение поля дата релиза {} по условию", film.getReleaseDate());
        if (film.getReleaseDate() == null || !checkReleaseDate(film.getReleaseDate())) {
            log.error("Дата релиза — не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        log.debug("Проверка на заполнение поля продолжительность фильма {} по условию", film.getDuration());
        if (checkDuration(film.getDuration())) {
            log.error("Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    private boolean checkReleaseDate(LocalDate releaseDate) {
        LocalDate dateOfBirthOfCinema = LocalDate.of(1895, 12, 28);
        return releaseDate.isAfter(dateOfBirthOfCinema) || releaseDate.isEqual(dateOfBirthOfCinema);
    }

    private boolean checkDuration(Long duration) {
        return duration == null || duration < 0;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
