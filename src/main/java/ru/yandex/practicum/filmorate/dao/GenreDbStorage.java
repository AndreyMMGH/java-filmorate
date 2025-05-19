package ru.yandex.practicum.filmorate.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Component
@Primary
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {

    private static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM genres";
    private static final String FIND_GENRES_BY_ID_QUERY = "SELECT * FROM genres WHERE id_genre = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
    }

    @Override
    public Collection<Genre> findAll() {
        return findMany(FIND_ALL_GENRES_QUERY);
    }

    @Override
    public Genre findGenreById(Long genreId) {
        Optional<Genre> genre = findOne(FIND_GENRES_BY_ID_QUERY, genreId);
        return genre.orElse(null);
    }
}
