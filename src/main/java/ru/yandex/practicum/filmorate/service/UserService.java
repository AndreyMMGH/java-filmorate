package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public User createUser(User user) {
        validateUser(user);
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validateUser(user);

        User existingUser = userStorage.findUserById(user.getId());
        if (existingUser == null) {
            log.warn("Попытка обновить несуществующего пользователя с id: {}", user.getId());
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден.");
        }

        return userStorage.updateUser(user);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
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
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
    }

    public void addToFriends(Long userId, Long userFriendId) {
        log.info("Процедура начала добавления друга с id {} у пользователя с id {}", userId, userFriendId);
        User user = userStorage.findUserById(userId);
        User userFriend = userStorage.findUserById(userFriendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с таким id: " + userId + " не найден");
        }
        if (userFriend == null) {
            throw new NotFoundException("Друг пользователя с таким id: " + userFriendId + " не найден");
        }

        userStorage.addToFriend(user, userFriend);
        log.info("Процедура добавления друга с id {} у пользователя с id {} завершена", userId, userFriendId);
    }

    public User findUserById(Long userId) {
        User user = userStorage.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с таким id: " + userId + " не найден");
        }
        return user;
    }

    public void removeFromFriends(Long userId, Long userFriendId) {
        log.info("Процедура начала удаления друга с id {} у пользователя с id {}", userId, userFriendId);
        User user = userStorage.findUserById(userId);
        User userFriend = userStorage.findUserById(userFriendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с таким id: " + userId + " не найден");
        }
        if (userFriend == null) {
            throw new NotFoundException("Друг пользователя с таким id: " + userFriendId + " не найден");
        }

        userStorage.removeFromFriends(userId, userFriendId);
        log.info("Процедура удаления друга с id {} у пользователя с id {} завершена", userId, userFriendId);
    }

    public List<User> getFriendsList(Long userId) {
        log.info("Процедура получения списка друзей у пользователя с id: {}", userId);
        User user = userStorage.findUserById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь с таким id: " + userId + " не найден");
        }

        Set<Long> friendsOfUserIds = user.getFriends();
        if (friendsOfUserIds == null || friendsOfUserIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<User> friendsOfUser = friendsOfUserIds.stream()
                .map(userStorage::findUserById)
                .filter(Objects::nonNull)
                .toList();

        log.info("Cписок друзей у пользователя с id: {} сформирован", userId);
        return friendsOfUser;
    }

    public List<User> listMutualFriends(Long userId, Long otherUserId) {
        log.info("Начата процедура получения списка общих друзей у одного пользователя с id {} и другого с id {}", userId, otherUserId);

        User user = findUserById(userId);
        User otherUser = findUserById(otherUserId);

        if (user == null) {
            throw new ValidationException("Пользователь с таким id: " + userId + " не найден");
        }

        if (otherUser == null) {
            throw new ValidationException("Друг пользователя с таким id: " + otherUserId + " не найден");
        }

        Set<Long> userFriends = user.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();

        Set<Long> mutualFriendsIds = new HashSet<>(userFriends);
        mutualFriendsIds.retainAll(otherUserFriends);

        List<User> mutualFriends = mutualFriendsIds.stream()
                .map(userStorage::findUserById)
                .filter(Objects::nonNull)
                .toList();

        log.info("Список общих друзей сформирован");

        return mutualFriends;
    }
}
