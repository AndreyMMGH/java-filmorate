package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserController {
    private final Map<String, User> users = new HashMap<>();

    @PostMapping
    public User create(@RequestBody User user) {
        return ;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        return;
    }

    @GetMapping
    public Collection<User> findAll() {
        return ;
    }

    private long getNextId() {
        return ;
    }
}
