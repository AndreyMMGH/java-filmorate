package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.debug("Фильм {} добавлен!", film);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        log.debug("Проверка на заполнение поля id {} по условию", newFilm.getId());
        if (newFilm.getId() == null) {
            log.error("Id должен быть указан");
            throw new ValidationException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            log.debug("Фильмы из хранилища: {}", newFilm);
            validateFilm(newFilm);
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setLikes((newFilm.getLikes()));

            log.debug("Фильм {} обновлен", oldFilm);
            return oldFilm;
        }
        log.error("Фильм с данным id - {} не найден", newFilm.getId());
        throw new NotFoundException("Фильм с данным id - " + newFilm.getId() + " не найден");
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
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
        log.debug("Проверка на заполнение поля Лайки {} по условию", film.getLikes());
        if (film.getLikes() == null) {
            log.error("Список лайков не может быть null");
            throw new ValidationException("Список лайков не может быть null");
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

    @Override
    public Film findFilmById(Long filmId) {
        return films.get(filmId);
    }
}
