package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film findFilmById(Long filmId);

    Film updateFilm(Film newFilm);

    List<Film> findAll();
}
