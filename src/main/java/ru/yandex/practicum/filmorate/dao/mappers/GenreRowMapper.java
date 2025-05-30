package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreRowMapper implements RowMapper<Genre> {
    @Override
    public Genre mapRow(ResultSet resultSet, int rowMum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getLong("id_genre"));
        genre.setName(resultSet.getString("name"));
        return genre;
    }
}
