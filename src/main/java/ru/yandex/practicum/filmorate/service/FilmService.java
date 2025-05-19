package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public Film createFilm(Film film) {
        validateFilm(film);
        updateFields(film);
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        updateFields(film);
        return filmStorage.updateFilm(film);
    }

    public List<Film> findAll() {
        return filmStorage.findAll();
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
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
    }

    private boolean checkReleaseDate(LocalDate releaseDate) {
        LocalDate dateOfBirthOfCinema = LocalDate.of(1895, 12, 28);
        return releaseDate.isAfter(dateOfBirthOfCinema) || releaseDate.isEqual(dateOfBirthOfCinema);
    }

    private boolean checkDuration(Long duration) {
        return duration == null || duration < 0;
    }

    public void addingLikeMovie(Long filmId, Long userId) {
        Film film = filmStorage.findFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с таким id " + filmId + " не найден");
        }

        User user = userStorage.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с таким id " + userId + " не найден");
        }

        filmStorage.addingLikeMovie(film, user);
    }

    public Film findFilmById(Long filmId) {
        Film film = filmStorage.findFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с данным id: " + filmId + " не найден");
        }
        updateFields(film);
        return film;
    }

    public void removeLikeFromMovie(Long filmId, Long userId) {
        Film film = filmStorage.findFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с таким id " + filmId + " не найден");
        }

        User user = userStorage.findUserById(userId);
        if (user == null) {
            throw new ValidationException("Пользователь с таким id " + userId + " не найден");
        }

        filmStorage.removeLikeFromMovie(filmId, userId);
    }


    public List<Film> outputOfPopularMovies(int count) {

        List<Film> allFilms = filmStorage.findAll();

        if (allFilms == null || allFilms.isEmpty()) {
            throw new NotFoundException("Список allFilms пустой или содержит null");
        }

        return allFilms.stream()
                .sorted((film1, film2) ->
                        Integer.compare((film2.getLikes() != null) ? film2.getLikes().size() : 0,
                                (film1.getLikes() != null) ? film1.getLikes().size() : 0))
                .limit(count)
                .collect(Collectors.toList());
    }

    private void updateFields(Film film) {
        Mpa mpa = mpaStorage.findMpaById(film.getMpa().getId());
        film.setMpa(checkMpa(mpa, film.getMpa().getId()));

        Set<Genre> genres = checkGenresExist(film.getGenres());
        film.setGenres(genres);
    }

    private Mpa checkMpa(Mpa mpa, Long mpaId) {
        if (mpa == null) {
            throw new NotFoundException("Рейтинг с данным id " + mpaId + " не найден");
        }
        return mpa;
    }

    private Set<Genre> checkGenresExist(Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return Collections.emptySet();
        }

        return genres.stream()
                .map(genre -> {
                    Genre existingGenre = genreStorage.findGenreById(genre.getId());
                    if (existingGenre == null) {
                        throw new NotFoundException("Жанр с данным id " + genre.getId() + " не найден");
                    }
                    return existingGenre;
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
