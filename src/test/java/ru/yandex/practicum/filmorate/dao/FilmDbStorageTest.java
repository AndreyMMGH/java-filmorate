package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class})
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    @Test
    public void testShouldCreateFilm() {
        Film film = new Film();
        film.setName("1+1");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2011, 9, 23));
        film.setDuration(110L);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        film.setMpa(mpa);

        filmStorage.create(film);

        Long id = film.getId();
        Film resultFilm = filmStorage.findFilmById(id);

        assertNotNull(resultFilm, "Фильм не найден");
        assertEquals(film.getName(), resultFilm.getName(), "Наименование фильма не совпадает");
        assertEquals(film.getDescription(), resultFilm.getDescription(), "Описание фильма не совпадает");
        assertEquals(film.getReleaseDate(), resultFilm.getReleaseDate(), "Дата выхода фильма не совпадает");
        assertEquals(film.getDuration(), resultFilm.getDuration(), "Длительность фильма не совпадает");
        assertEquals(film.getMpa().getId(), resultFilm.getMpa().getId(), "Неверный рейтинг добавленного фильма");
    }

    @Test
    void testShouldFindFilmById() {
        Film film = new Film();
        film.setName("1+1");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2011, 9, 23));
        film.setDuration(110L);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        film.setMpa(mpa);

        Film film2 = new Film();
        film2.setName("Джентльмены");
        film2.setDescription("Описание джентльменов");
        film2.setReleaseDate(LocalDate.of(2020, 2, 13));
        film2.setDuration(113L);

        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        mpa2.setName("PG");
        film2.setMpa(mpa2);

        filmStorage.create(film);
        filmStorage.create(film2);

        Long id = film.getId();
        Film resultFilm = filmStorage.findFilmById(id);

        assertNotNull(resultFilm, "Фильм не найден");
        assertEquals(film.getName(), resultFilm.getName(), "Наименование фильма не совпадает");
        assertEquals(film.getDescription(), resultFilm.getDescription(), "Описание фильма не совпадает");
        assertEquals(film.getReleaseDate(), resultFilm.getReleaseDate(), "Дата выхода фильма не совпадает");
        assertEquals(film.getDuration(), resultFilm.getDuration(), "Длительность фильма не совпадает");
        assertEquals(film.getMpa().getId(), resultFilm.getMpa().getId(), "Неверный рейтинг добавленного фильма");
    }

    @Test
    void testShouldUpdateFilm() {
        Film film = new Film();
        film.setName("1+1");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2011, 9, 23));
        film.setDuration(110L);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        film.setMpa(mpa);

        Film createdFilm = filmStorage.create(film);
        Long filmId = createdFilm.getId();

        Film filmToUpdate = new Film();
        filmToUpdate.setId(filmId);
        filmToUpdate.setName("1+1 (Обновлен)");
        filmToUpdate.setDescription("Обновленное описание фильма");
        filmToUpdate.setReleaseDate(LocalDate.of(2012, 10, 24));
        filmToUpdate.setDuration(120L);

        Mpa mpaToUpdate = new Mpa();
        mpaToUpdate.setId(2);
        mpaToUpdate.setName("PG");
        filmToUpdate.setMpa(mpaToUpdate);

        filmStorage.updateFilm(filmToUpdate);
        Film resultFilm = filmStorage.findFilmById(filmId);

        assertNotNull(resultFilm, "Фильм не найден");
        assertEquals("1+1 (Обновлен)", resultFilm.getName(), "Наименование фильма должно совпасть");
        assertEquals("Обновленное описание фильма", resultFilm.getDescription(), "Описание фильма должно совпасть");
        assertEquals(LocalDate.of(2012, 10, 24), resultFilm.getReleaseDate(), "Дата выхода фильма должна совпасть");
        assertEquals(120L, resultFilm.getDuration(), "Длительность фильма должна совпасть");
        assertEquals(2, resultFilm.getMpa().getId(), "Mpa должно быть обновлено");
    }

    @Test
    void testShouldFindAll() {
        Film film = new Film();
        film.setName("1+1");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2011, 9, 23));
        film.setDuration(110L);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        film.setMpa(mpa);

        filmStorage.create(film);
        filmStorage.create(film);
        assertEquals(2, filmStorage.findAll().size(), "Другое количество фильмов");
    }

}
