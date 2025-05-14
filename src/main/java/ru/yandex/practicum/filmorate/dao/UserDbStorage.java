package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String FIND_USER_BY_ID_QUERY = "SELECT id_user, email, login, name, birthday FROM users WHERE id_user = ?";
    private static final String FIND_ALL_USERS_QUERY = "SELECT id_user, email, login, name, birthday FROM users";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id_user = ?";
    private static final String DELETE_USER_QUERY = "DELETE FROM users WHERE id_user = ?";
    private static final String FIND_FRIENDS_BY_USER_ID_QUERY = "SELECT id_friend_user FROM friends WHERE id_user = ?";

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User(
                rs.getLong("id_user"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate(),
                null,
                null
        );

        user.setFriends(getFriendsForUser(user.getId()));

        return user;
    };

    @Override
    public User createUser(User user) {

    }


    @Override
    public User findUserById(Long userId) {

    }

    @Override
    public User updateUser(User newUser) {

    }

    @Override
    public void deleteUser(Long userId) {

    }

    @Override
    public Collection<User> findAll() {

    }

    private java.util.Set<Long> getFriendsForUser(Long userId) {

    }
}