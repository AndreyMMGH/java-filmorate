package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User createUser(@RequestBody User user) {
        validateUser(user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("Пользователь {} добавлен!", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        log.debug("Проверка на заполнение поля id {} по условию", newUser.getId());
        if (newUser.getId() == null) {
            log.error("Id пользователя должен быть указан");
            throw new ValidationException("Id пользователя должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            log.debug("Пользователи из хранилища: {}", newUser);
            validateUser(newUser);
            User oldUser = users.get(newUser.getId());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            log.debug("Пользователь {} обновлен", oldUser);
            return oldUser;
        }
        log.error("Пользователь с данным id - {} не найден", newUser.getId());
        throw new NotFoundException("Пользователь с данным id - " + newUser.getId() + " не найден");
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    private void validateUser(User user) {
        log.debug("Проверка на заполнение поля электронная почта {} по условию", user.getEmail());
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        log.debug("Проверка на заполнение поля логин {} по условию", user.getLogin());
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.debug("Проверка на заполнение поля дата рождения {} по условию", user.getBirthday());
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем.");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
