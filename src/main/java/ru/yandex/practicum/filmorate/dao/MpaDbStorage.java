package ru.yandex.practicum.filmorate.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.Optional;

@Component
@Primary
public class MpaDbStorage extends BaseDbStorage<Mpa> implements MpaStorage {

    private static final String FIND_ALL_RATINGS_QUERY = "SELECT * FROM ratings";
    private static final String FIND_RATINGS_BY_ID_QUERY = "SELECT * FROM ratings WHERE id_rating = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper, Mpa.class);
    }

    @Override
    public Collection<Mpa> findAll() {
        return findMany(FIND_ALL_RATINGS_QUERY);
    }

    @Override
    public Mpa findMpaById(Long mpaId) {
        Optional<Mpa> mpa = findOne(FIND_RATINGS_BY_ID_QUERY, mpaId);
        return mpa.orElse(null);
    }
}
