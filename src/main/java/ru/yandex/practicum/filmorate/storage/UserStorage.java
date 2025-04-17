package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface UserStorage {

    User createUser(User user);

    User findUserById(Long userId);

    User updateUser(User newUser);

    void deleteUser(Long userId);

    Collection<User> findAll();

}
