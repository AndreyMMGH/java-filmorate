package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
@Primary
//@RequiredArgsConstructor
@Slf4j
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    private static final String INSERT_USER_QUERY = "INSERT INTO users (email, login, name, birthday)" +
                                                    "VALUES (?, ?, ?, ?)";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT u.*, (SELECT Listagg(f.id_friend_user, ',') FROM friends f WHERE f.id_user = u.id_user) AS user_friends " +
                                                    "FROM users u WHERE u.id_user = ?";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id_user = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id_user = ?";
    private static final String FIND_ALL_USERS_QUERY = "SELECT u.*, (SELECT Listagg(f.id_friend_user, ',') FROM friends f WHERE f.id_user = u.id_user) AS user_friends FROM users u";
    private static final String ADD_FRIEND_QUERY = "MERGE INTO friends KEY(id_user, id_friend_user) values(?, ?)";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friends WHERE id_user = ? AND id_friend_user =?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }

    @Override
    public User createUser(User user) {
        long id = insert(
                INSERT_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User findUserById(Long userId) {
        Optional<User> user = findOne(FIND_USER_BY_ID_QUERY, userId);
        return user.orElse(null);
    }

    @Override
    public User updateUser(User newUser) {
        update(
                UPDATE_USER_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId()
        );
        return newUser;
    }

    @Override
    public void deleteUser(Long userId) {
        delete(DELETE_USER_QUERY, userId);
    }

    @Override
    public Collection<User> findAll() {
        return findMany(FIND_ALL_USERS_QUERY);
    }

    @Override
    public void addToFriend(User user, User friend) {
        update(
                ADD_FRIEND_QUERY,
                user.getId(),
                friend.getId()
        );
    }

    @Override
    public void removeFromFriends(Long userId, Long userFriendId) {
        delete(
                DELETE_FRIEND_QUERY,
                userId,
                userFriendId
        );
    }
}
