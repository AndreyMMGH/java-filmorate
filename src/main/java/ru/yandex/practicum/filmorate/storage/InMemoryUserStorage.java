package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.debug("Пользователь {} добавлен!", user);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        log.debug("Проверка на заполнение поля id {} по условию", newUser.getId());
        if (newUser.getId() == null) {
            log.error("Id пользователя должен быть указан");
            throw new ValidationException("Id пользователя должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            log.debug("Пользователи из хранилища: {}", newUser);
            User oldUser = users.get(newUser.getId());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            oldUser.setFriends((newUser.getFriends()));
            log.debug("Пользователь {} обновлен", oldUser);
            return oldUser;
        }
        log.error("Пользователь с данным id - {} не найден", newUser.getId());
        throw new NotFoundException("Пользователь с данным id - " + newUser.getId() + " не найден");
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public User findUserById(Long userId) {
        return users.get(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    @Override
    public void addToFriend(User user, User userFriend) {
        user.getFriends().add(userFriend.getId());
        userFriend.getFriends().add(user.getId());
    }

    @Override
    public void removeFromFriends(Long userId, Long userFriendId) {
        users.get(userId).getFriends().remove(userFriendId);
        users.get(userFriendId).getFriends().remove(userId);
    }
}
