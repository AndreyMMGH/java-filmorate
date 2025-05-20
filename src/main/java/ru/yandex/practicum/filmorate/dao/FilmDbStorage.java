package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Primary
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    private static final String INSERT_FILMS_QUERY = "INSERT INTO films (name, description, id_rating, release_date, duration) VALUES (?, ?, ?, ?, ?)";
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM film_genres WHERE id_film = ?";
    private static final String INSERT_FILM_GENRES_QUERY = "MERGE INTO film_genres KEY(id_film, id_genre) VALUES(?, ?)";
    private static final String FIND_FILMS_BY_ID_QUERY = "SELECT f.*, " +
            "(SELECT Listagg(fg.id_genre, ',') FROM film_genres fg WHERE fg.id_film = f.id_film) as genres, " +
            "(SELECT Listagg(fl.id_user, ',') FROM film_likes fl WHERE fl.id_film = f.id_film) as likes " +
            "FROM films f WHERE f.id_film = ?";
    private static final String UPDATE_FILMS_QUERY = "UPDATE films SET name = ?, description = ?, id_rating = ?, release_date = ?, duration = ? WHERE id_film = ?";
    private static final String FIND_ALL_FILMS_QUERY = "SELECT f.*, " +
            "(SELECT Listagg(fg.id_genre, ',') FROM film_genres fg WHERE fg.id_film = f.id_film) as genres, " +
            "(SELECT Listagg(fl.id_user, ',') FROM film_likes fl WHERE fl.id_film = f.id_film) as likes " +
            "FROM films f";
    private static final String INSERT_FILMS_LIKES = "MERGE INTO film_likes KEY (id_film, id_user) VALUES (?, ?);";
    private static final String DELETE_FILMS_LIKES = "DELETE FROM film_likes WHERE id_film = ? AND id_user = ?";
    private static final String FIND_POPULAR_FILMS_QUERY = "SELECT f.*, " +
            "(SELECT COUNT(*) FROM film_likes fl WHERE fl.id_film = f.id_film) AS like_count, " +
            "(SELECT LISTAGG(fg.id_genre, ',') FROM film_genres fg WHERE fg.id_film = f.id_film) AS genres, " +
            "(SELECT LISTAGG(fl.id_user, ',') FROM film_likes fl WHERE fl.id_film = f.id_film) AS likes " +
            "FROM films f " +
            "ORDER BY like_count DESC " +
            "LIMIT ?";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);
    }

    @Override
    public Film create(Film film) {
        long id = insert(
                INSERT_FILMS_QUERY,
                film.getName(),
                film.getDescription(),
                film.getMpa().getId(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration()
        );
        updateGenres(id, getListGenresId(film.getGenres()));
        film.setId(id);
        return film;
    }

    private void updateGenres(Long filmId, List<Long> genres) {
        delete(DELETE_FILM_GENRES_QUERY, filmId);
        if (genres != null && !genres.isEmpty()) {
            genres.forEach(genreId -> update(
                            INSERT_FILM_GENRES_QUERY,
                            filmId,
                            genreId
                    )
            );
        }
    }

    private List<Long> getListGenresId(List<Genre> genres) {
        if (genres == null) {
            return null;
        }
        return genres.stream()
                .map(Genre::getId)
                .collect(Collectors.toList());

    }

    @Override
    public Film findFilmById(Long filmId) {
        Optional<Film> film = findOne(FIND_FILMS_BY_ID_QUERY, filmId);
        return film.orElse(null);
    }

    @Override
    public Film updateFilm(Film newFilm) {
        update(
                UPDATE_FILMS_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getMpa().getId(),
                Date.valueOf(newFilm.getReleaseDate()),
                newFilm.getDuration(),
                newFilm.getId()
        );
        updateGenres(newFilm.getId(), getListGenresId(newFilm.getGenres()));
        return newFilm;
    }

    @Override
    public List<Film> findAll() {
        return findMany(FIND_ALL_FILMS_QUERY);
    }

    @Override
    public void addingLikeMovie(Film film, User user) {
        update(INSERT_FILMS_LIKES,
                film.getId(),
                user.getId()
        );
    }

    @Override
    public void removeLikeFromMovie(Long filmId, Long userId) {
        delete(
                DELETE_FILMS_LIKES,
                filmId,
                userId
        );
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        return findMany(FIND_POPULAR_FILMS_QUERY, count);
    }
}
