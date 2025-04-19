package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmTest {
    /*FilmController filmController = new FilmController();
    Film film;*/

    /*@BeforeEach
    public void testFilm() {
        film = new Film(1L, "1+1", "Драма, комедия", LocalDate.parse("2011-09-23"), 112L);
    }*/

   /* @Test
    public void checkTheNameFieldOnNull() {
        film.setName(null);
        assertEquals("Название фильма не может быть пустым", assertThrows(ValidationException.class, () -> filmController.create(film)).getMessage());
    }

    @Test
    public void checkTheNameFieldOnIsEmpty() {
        film.setName("     ");
        assertEquals("Название фильма не может быть пустым", assertThrows(ValidationException.class, () -> filmController.create(film)).getMessage());
    }

    @Test
    public void checkTheDescriptionFieldForUpTo200Characters() {
        film.setDescription("Очень много символов, Очень много символов, Очень много символов, Очень много символов, Очень много символов, Очень много символов, Очень много символов, Очень много символов, Очень много символов, Очень много символов");
        assertEquals("Максимальная длина описания — 200 символов", assertThrows(ValidationException.class, () -> filmController.create(film)).getMessage());
    }

    @Test
    public void checkTheReleaseDateField() {
        LocalDate releaseDate = film.getReleaseDate();
        film.setReleaseDate(releaseDate.minusYears(200));
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года", assertThrows(ValidationException.class, () -> filmController.create(film)).getMessage());
    }

    @Test
    public void checkTheDurationField() {
        Long durationFilm = film.getDuration();
        film.setDuration(durationFilm - 300);
        assertEquals("Продолжительность фильма должна быть положительным числом", assertThrows(ValidationException.class, () -> filmController.create(film)).getMessage());
    }

    @Test
    public void checkingTheIdFieldFilm() {
        film.setId(2L);
        assertEquals("Фильм с данным id - " + film.getId() + " не найден", assertThrows(NotFoundException.class, () -> filmController.updateFilm(film)).getMessage());
    }

    @Test
    public void checkingTheIdFieldOnNullFilm() {
        film.setId(null);
        assertEquals("Id должен быть указан", assertThrows(ValidationException.class, () -> filmController.updateFilm(film)).getMessage());
    }*/

}