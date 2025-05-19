package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id_film"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setMpa(converToMpa(resultSet.getLong("id_rating")));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getLong("duration"));
        String likesBd = resultSet.getString("likes");
        film.setLikes(split(likesBd));
        film.setLikes(split(likesBd));

        Set<Genre> genres = split(resultSet.getString("genres"))
                .stream()
                .map(this::converToGenre)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        film.setGenres(genres);

        return film;
    }

    private Mpa converToMpa(Long id) {
        if (id == 0) {
            return null;
        }
        Mpa mpa = new Mpa();
        mpa.setId(id);
        return mpa;
    }

    private Set<Long> split(String str) {
        if (str == null || str.isBlank()) {
            return Collections.emptySet();
        }

        return Arrays.stream(str.split(","))
                .map(Long::parseLong)
                .sorted()
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Genre converToGenre(Long id) {
        Genre genre = new Genre();
        genre.setId(id);
        return genre;
    }
}

