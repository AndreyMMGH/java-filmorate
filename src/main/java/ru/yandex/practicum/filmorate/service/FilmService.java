package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
    }

    //вывод 10 наиболее популярных фильмов по количеству лайков
    public List<Film> outputOfPopularMovies(int count) {
        List<Film> allFilms = filmStorage.findAll();

        Collections.sort(allFilms, new Comparator<Film>() {
            @Override
            public int compare(Film film1, Film film2) {
                return Integer.compare(film2.getLikes().size(), film1.getLikes().size());
            }
        });

        if (allFilms.size()>count) {
            allFilms = allFilms.subList(0, count);
        }

        return allFilms;
    }
}
