package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    User createUser(User user);

    User findUserById(Long userId);

    User updateUser(User newUser);

    void deleteUser(Long userId);

    Collection<User> findAll();

    void addToFriend(User user, User friend);

    void removeFromFriends(Long userId, Long userFriendId);

    List<User> getFriendsList(Long userId);

    List<User> getListMutualFriends(Long userId, Long otherUserId);
}
