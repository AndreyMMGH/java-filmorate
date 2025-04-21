package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
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
