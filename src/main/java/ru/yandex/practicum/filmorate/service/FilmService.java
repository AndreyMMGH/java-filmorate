package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.create(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    //добавление лайка к фильму
    //пользователь может поставить лайк фильму только один раз
    public void addingLikeMovie (Long filmId, Long userId) {
        Film film = findFilmById(filmId);
        if (film == null) {
            throw new ValidationException("Фильм с таким id " + filmId + " не найден");
        }
        if (userId == null) {
            throw new ValidationException("Пользователь с данным id: " + filmId + " не найден");
        }
        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    public Film findFilmById(Long filmId) {
        Film film = filmStorage.findFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с данным id: " + filmId + " не найден");
        }
        return film;
    }

    //удаление лайка у фильма
    public void removeLikeFromMovie(Long filmId, Long userId) {
        Film film = findFilmById(filmId);
        if (film == null) {
            throw new ValidationException("Фильм с таким id " + filmId + " не найден");
        }
        if (userId == null) {
            throw new ValidationException("Пользователь с данным id: " + filmId + " не найден");
        }
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
    }

    //вывод 10 наиболее популярных фильмов по количеству лайков
    public List<Film> outputOfPopularMovies(int count) {
        List<Film> allFilms = filmStorage.findAll();

        if (allFilms == null || allFilms.isEmpty()) {
            throw new ValidationException("Список allFilms пустой или содержит null");
        }

        Collections.sort(allFilms, new Comparator<Film>() {
            @Override
            public int compare(Film film1, Film film2) {
                return Integer.compare((film2.getLikes()!= null) ? film2.getLikes().size() : 0,
                        (film1.getLikes()!= null) ? film1.getLikes().size() : 0);
            }
        });

        if (allFilms.size()>count) {
            allFilms = allFilms.subList(0, count);
        }

        return allFilms;
    }
}
