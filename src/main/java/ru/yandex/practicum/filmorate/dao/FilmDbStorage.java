package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_INSERT_FILM = "INSERT INTO films (name, description, id_rating, release_date, duration) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_FILM = "UPDATE films SET name = ?, description = ?, id_rating = ?, release_date = ?, duration = ? WHERE id_film = ?";
    private static final String SQL_SELECT_FILM_BY_ID = "SELECT f.id_film, f.name, f.description, f.release_date, f.duration, r.id_rating, r.name AS rating_name FROM films AS f JOIN ratings AS r ON f.id_rating = r.id_rating WHERE f.id_film = ?";
    private static final String SQL_SELECT_ALL_FILMS = "SELECT f.id_film, f.name, f.description, f.release_date, f.duration, r.id_rating, r.name AS rating_name FROM films AS f JOIN ratings AS r ON f.id_rating = r.id_rating";
    private static final String SQL_DELETE_FILM_GENRES = "DELETE FROM film_genres WHERE id_film = ?";
    private static final String SQL_INSERT_FILM_GENRES = "INSERT INTO film_genres (id_film, id_genre) VALUES (?, ?)";
    private static final String SQL_SELECT_FILM_GENRES = "SELECT g.id_genre, g.name FROM film_genres AS fg JOIN genres AS g ON fg.id_genre = g.id_genre WHERE fg.id_film = ?";
    private static final String SQL_SELECT_FILM_LIKES = "SELECT id_user FROM film_likes WHERE id_film = ?";
    private static final String SQL_INSERT_FILM_LIKES = "INSERT INTO film_likes (id_film, id_user) VALUES (?, ?)";
    private static final String SQL_DELETE_FILM_LIKES = "DELETE FROM film_likes WHERE id_film = ? AND id_user = ?";

    private final RowMapper<Film> filmRowMapper = (rs, rowNum) -> {
        Film film = new Film(
                rs.getLong("id_film"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getLong("duration"),
                //rs.getLong("id_rating")
                new HashSet<>(),
                new Mpa(rs.getInt("id_rating"), rs.getString("rating_name")),
                new HashSet<>()
        );
        return film;
    };

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_FILM, new String[]{"id_film"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setLong(3, film.getMpa().getId());
            ps.setLong(3, film.getMpa().getId());
            ps.setDate(4, Date.valueOf(film.getReleaseDate()));
            ps.setLong(5, film.getDuration());
            return ps;
        }, keyHolder);

        Long filmId = keyHolder.getKey().longValue();
        film.setId(filmId);

        if (film.getGenres() != null) {
            updateFilmGenres(film);
        }

        log.info("Фильм {} добавлен!", film);
        return findFilmById(filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        int updateCount = jdbcTemplate.update(SQL_UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getMpa().getId(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getId());

        if (updateCount == 0) {
            log.error("Фильм с данным id - {} не найден", film.getId());
            throw new NotFoundException("Фильм с данным id - " + film.getId() + " не найден");
        }

        if (film.getGenres() != null) {
            updateFilmGenres(film);
        } else {
            jdbcTemplate.update(SQL_DELETE_FILM_GENRES, film.getId());
        }

        log.info("Фильм {} обновлен", film);
        return findFilmById(film.getId());
    }

    private void updateFilmGenres(Film film) {
        jdbcTemplate.update(SQL_DELETE_FILM_GENRES, film.getId());

        Set<Genre> genres = film.getGenres();
        if (genres != null && !genres.isEmpty()) {
            for (Genre genre : genres) {
                jdbcTemplate.update(SQL_INSERT_FILM_GENRES, film.getId(), genre.getId());
            }
        }
    }

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query(SQL_SELECT_ALL_FILMS, filmRowMapper);
        films.forEach(film -> {
            film.setGenres(getFilmGenres(film.getId()));
            film.setLikes(getFilmLikes(film.getId()));
        });
        return films;
    }

    @Override
    public Film findFilmById(Long filmId) {
        Film film;
        try {
            film = jdbcTemplate.queryForObject(SQL_SELECT_FILM_BY_ID, filmRowMapper, filmId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Фильм с данным id - {} не найден", filmId);
            throw new NotFoundException("Фильм с данным id - " + filmId + " не найден");
        }

        if (film != null) {
            film.setGenres(getFilmGenres(filmId));
            film.setLikes(getFilmLikes(filmId));
        }

        return film;
    }

    private Set<Genre> getFilmGenres(Long filmId) {
        return new HashSet<>(jdbcTemplate.query(SQL_SELECT_FILM_GENRES, (rs, rowNum) -> new Genre(rs.getInt("id_genre"), rs.getString("name")), filmId));
    }

    private Set<Long> getFilmLikes(Long filmId) {
        return new HashSet<>(jdbcTemplate.query(SQL_SELECT_FILM_LIKES, (rs, rowNum) -> rs.getLong("id_user"), filmId));
    }

    public void addLike(Long filmId, Long userId) {
        jdbcTemplate.update(SQL_INSERT_FILM_LIKES, filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        jdbcTemplate.update(SQL_DELETE_FILM_LIKES, filmId, userId);
    }
}