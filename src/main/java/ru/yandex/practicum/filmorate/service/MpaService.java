package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;

@RequiredArgsConstructor
@Slf4j
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public Collection<Mpa> findAll() {
        return mpaStorage.findAll();
    }

    public Mpa findMpaById(Long mpaId) {
        Mpa mpa = mpaStorage.findMpaById(mpaId);
        if (mpa == null) {
            throw new NotFoundException("Рейтинг с данным id " + mpaId + " не найден");
        }
        return mpa;
    }
}
