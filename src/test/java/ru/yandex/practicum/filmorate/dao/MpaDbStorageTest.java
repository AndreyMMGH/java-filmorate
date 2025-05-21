package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class, MpaRowMapper.class})
public class MpaDbStorageTest {
    private final MpaDbStorage mpaStorage;

    @Test
    void testShouldReturnAllMpaRatings() {
        Collection<Mpa> mpaList = mpaStorage.findAll();

        List<Mpa> expectedMpaList = List.of(
                new Mpa(1L, "G"),
                new Mpa(2L, "PG"),
                new Mpa(3L, "PG-13"),
                new Mpa(4L, "R"),
                new Mpa(5L, "NC-17")
        );

        assertNotNull(mpaList, "Список рейтингов не должен быть null");
        assertEquals(expectedMpaList.size(), mpaList.size(), "Количество записей не совпадает");
        assertThat(mpaList).usingRecursiveFieldByFieldElementComparator().containsExactlyElementsOf(expectedMpaList);
    }

    @Test
    void testShouldReturnMpaById() {
        Long mpaId = 5L;
        String expectedName = "NC-17";

        Mpa mpaFromDb = mpaStorage.findMpaById(mpaId);

        assertNotNull(mpaFromDb, "Рейтинг MPA с данным id" + mpaId + " не найден");
        assertEquals(expectedName, mpaFromDb.getName(), "Название рейтинга не совпадает");
        assertEquals(mpaId, mpaFromDb.getId(), "Id рейтинга не совпадает");
    }
}
