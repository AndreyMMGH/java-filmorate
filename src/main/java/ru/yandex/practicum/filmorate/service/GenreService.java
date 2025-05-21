package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;

@RequiredArgsConstructor
@Slf4j
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    public Collection<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Genre findGenreById(Long genreId) {
        Genre genre = genreStorage.findGenreById(genreId);
        if (genre == null) {
            throw new NotFoundException("Жанр с данным id " + genreId + " не найден");
        }
        return genre;
    }

}
