package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    //добавление в друзья
    public void addToFriends(Long userId, Long userFriendId) {
        User user = findUserById(userId);
        User userFriend = findUserById(userFriendId);

        if (user == null) {
            throw new ValidationException("Пользователь с таким id: " + userId + " не найден");
        }

        if (userFriend == null) {
            throw new ValidationException("Друг пользователя с таким id: " + userFriendId + " не найден");
        }

        user.getFriends().add(userFriendId);
        userFriend.getFriends().add(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(userFriend);
    }

    public User findUserById(Long userId) {
        User user = userStorage.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с таким id: " + userId + " не найден");
        }
        return user;
    }

    //удаление из друзей
    public void removeFromFriends(Long userId, Long userFriendId) {
        User user = findUserById(userId);
        User userFriend = findUserById(userFriendId);

        if (user == null) {
            throw new ValidationException("Пользователь с таким id: " + userId + " не найден");
        }

        if (userFriend == null) {
            throw new ValidationException("Друг пользователя с таким id: " + userFriendId + " не найден");
        }

        user.getFriends().remove(userFriendId);
        userFriend.getFriends().remove(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(userFriend);
    }

    //получение списка друзей
    public List<User> getFriendsList(Long userId) {
        User user = userStorage.findUserById(userId);

        if (user == null) {
            throw new ValidationException("Пользователь с таким id: " + userId + " не найден");
        }

        Set<Long> friendsOfUserIds = user.getFriends();

        if (friendsOfUserIds == null || friendsOfUserIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<User> friendsOfUser = new ArrayList<>();
        for (Long friendsOfUserId : friendsOfUserIds) {
            User friendOfUser = userStorage.findUserById(friendsOfUserId);
            if (friendOfUser != null) {
                friendsOfUser.add(friendOfUser);
            }
        }

        return friendsOfUser;
    }

    //вывод списка общих друзей
    public List<User> listMutualFriends(Long userId, Long otherUserId) {
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

        List<User> mutualFriends = new ArrayList<>();
        for (Long id : mutualFriendsIds) {
            User userFound = userStorage.findUserById(id);
            if (userFound != null) {
                mutualFriends.add(userFound);
            }
        }

        return mutualFriends;
    }
}
