package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class})
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;

    @Test
    public void testShouldCreateFilm() {
        Film film = new Film();

        film.setName("1+1");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2011, 9, 23));
        film.setDuration(110L);

        Genre genre = new Genre();
        genre.setId(1);
        genre.setName("Комедия");
        Set<Genre> genres = new HashSet<>();
        genres.add(genre);
        film.setGenres(genres);

        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");
        film.setMpa(mpa);

        filmStorage.create(film);

        List<Film> films = filmStorage.findAll();

        assertThat(films)
                .extracting(Film::getName)
                .contains("1+1");
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
        film.setMpa(mpa);

        Set<Genre> genres = new HashSet<>();
        Genre genre = new Genre();
        genre.setId(1);
        genres.add(genre);
        film.setGenres(genres);

        Film newFilm = filmStorage.create(film);
        Film foundFilm = filmStorage.findFilmById(newFilm.getId());

        assertThat(foundFilm).isNotNull();
        assertThat(foundFilm.getName()).isEqualTo("1+1");
        assertThat(foundFilm.getGenres()).isNotEmpty();
        assertThat(foundFilm.getGenres().iterator().next().getId()).isEqualTo(1L);
        assertThat(foundFilm.getLikes()).size();
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
        film.setMpa(mpa);

        Set<Genre> genres = new HashSet<>();
        Genre genre = new Genre();
        genre.setId(1);
        genres.add(genre);
        film.setGenres(genres);

        Film createdFilm = filmStorage.create(film);

        createdFilm.setName("Джентльмены");
        createdFilm.setDescription("Описание Джентльменов");
        createdFilm.setDuration(113L);
        createdFilm.setReleaseDate(LocalDate.of(2020, 2, 13));

        Set<Genre> updatedGenres = new HashSet<>();
        Genre anotherGenre = new Genre();
        anotherGenre.setId(2);
        updatedGenres.add(anotherGenre);
        createdFilm.setGenres(updatedGenres);

        Film updatedFilm = filmStorage.updateFilm(createdFilm);

        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getName()).isEqualTo("Джентльмены");
        assertThat(updatedFilm.getDescription()).isEqualTo("Описание Джентльменов");
        assertThat(updatedFilm.getDuration()).isEqualTo(113L);
        assertThat(updatedFilm.getReleaseDate()).isEqualTo(LocalDate.of(2020, 2, 13));
        assertThat(updatedFilm.getGenres())
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(2);
    }

    @Test
    void testShouldFindAll() {

        Film film1 = new Film();
        film1.setName("1+1");
        film1.setDescription("Описание 1+1");
        film1.setReleaseDate(LocalDate.of(2011, 9, 23));
        film1.setDuration(110L);

        Mpa mpa1 = new Mpa();
        mpa1.setId(1);
        film1.setMpa(mpa1);

        Set<Genre> genres1 = new HashSet<>();
        Genre genre1 = new Genre();
        genre1.setId(1);
        genres1.add(genre1);
        film1.setGenres(genres1);

        Film createdFilm1 = filmStorage.create(film1);

        Film film2 = new Film();
        film2.setName("Джентльмены");
        film2.setDescription("Описание джентльменов");
        film2.setReleaseDate(LocalDate.of(2020, 2, 13));
        film2.setDuration(113L);

        Mpa mpa2 = new Mpa();
        mpa2.setId(2);
        film2.setMpa(mpa2);

        Set<Genre> genres2 = new HashSet<>();
        Genre genre2 = new Genre();
        genre2.setId(2);
        genres2.add(genre2);
        film2.setGenres(genres2);

        Film createdFilm2 = filmStorage.create(film2);

        List<Film> allFilms = filmStorage.findAll();

        assertThat(allFilms).isNotEmpty();
        assertThat(allFilms).hasSize(2);

        Film returnedFilm1 = allFilms.get(0);
        assertThat(returnedFilm1.getName()).isEqualTo(createdFilm1.getName());
        assertThat(returnedFilm1.getDescription()).isEqualTo(createdFilm1.getDescription());
        assertThat(returnedFilm1.getGenres()).isNotNull().hasSize(1);
        assertThat(returnedFilm1.getGenres().iterator().next().getId()).isEqualTo(1L);
        assertThat(returnedFilm1.getLikes()).size();

        Film returnedFilm2 = allFilms.get(1);
        assertThat(returnedFilm2.getName()).isEqualTo(createdFilm2.getName());
        assertThat(returnedFilm2.getDescription()).isEqualTo(createdFilm2.getDescription());
        assertThat(returnedFilm2.getGenres()).isNotNull().hasSize(1);
        assertThat(returnedFilm2.getGenres().iterator().next().getId()).isEqualTo(2L);
        assertThat(returnedFilm2.getLikes()).size();
    }

}