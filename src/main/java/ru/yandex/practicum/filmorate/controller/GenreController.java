package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> findAll() {
        log.info("Вывод всех жанров");
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    public Genre findGenreById(@PathVariable("id") Long id) {
        log.info("Получение жанра по id {}", id);
        return genreService.findGenreById(id);
    }
}

