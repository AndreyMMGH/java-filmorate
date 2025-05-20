package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class, GenreRowMapper.class})
public class GenreDbStorageTest {
    private final GenreDbStorage genreStorage;

    @Test
    void shouldReturnSixGenres() {

        Collection<Genre> genreList = genreStorage.findAll();

        assertNotNull(genreList, "Список жанров не должен быть null");
        assertEquals(6, genreList.size(), "Количество жанров должно быть равно 6");

        List<Genre> expectedGenres = Arrays.asList(
                new Genre(1L, "Комедия"),
                new Genre(2L, "Драма"),
                new Genre(3L, "Мультфильм"),
                new Genre(4L, "Триллер"),
                new Genre(5L, "Документальный"),
                new Genre(6L, "Боевик")
        );

        assertThat(genreList).usingRecursiveComparison().isEqualTo(expectedGenres);
    }

    @Test
    void shouldReturnGenreById() {
        Long genreId = 5L;
        String expectedName = "Документальный";

        Genre dbGenre = genreStorage.findGenreById(genreId);

        assertNotNull(dbGenre, "Жанр с данным id" + genreId + " не найден");
        assertEquals(expectedName, dbGenre.getName(), "Название жанра не совпадает");
        assertEquals(genreId, dbGenre.getId(), "Id жанра не совпадает");
    }
}
