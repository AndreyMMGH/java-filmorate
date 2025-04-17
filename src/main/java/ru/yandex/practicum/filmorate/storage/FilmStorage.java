package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    Film create(Film film);

    Film updateFilm(Film newFilm);
}
