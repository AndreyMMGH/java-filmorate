package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.StatusFriendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;

@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final String CREATE_USER_QUERY = "INSERT INTO users (email, login, name, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE user SET email = ?, login = ?, name = ?, birthday = ? WHERE id_user = ?";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE id_user = ?";
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String DELETE_USER = "DELETE FROM users WHERE id_user = ?";
    private static final String GET_FRIENDS = "SELECT u.* FROM friends f JOIN users u ON f.id_friend_user = u.id_user" +
            "WHERE f.id_user = ? AND f.id_status_friendship = ?";
    private static final String GET_STATUS_FRIENDSHIP = "SELECT id_status_friendship FROM status_friendship WHERE name = ?";
    private static final String ADD_FRIEND = "INSERT INTO friends (id_user, id_friend_user, id_status_friendship) VALUES (?, ?, ?)";

    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User(
                rs.getObject("id_user", Long.class),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getObject("birthday", LocalDate.class),
                new HashSet<>(),
                null
        );
        return user;
    };

    @Override
    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_USER_QUERY, new String[]{"id_user"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.debug("Пользователь {} добавлен в базу данных!", user);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            log.error("Id пользователя должен быть указан");
            throw new ValidationException("Id пользователя должен быть указан");
        }
        int rowsAffected = jdbcTemplate.update(UPDATE_USER_QUERY,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getName(),
                newUser.getBirthday(),
                newUser.getId());

        if (rowsAffected == 0) {
            throw new NotFoundException("Пользователь с id " + newUser.getId() + " не найден.");
        }
        log.debug("Пользователь {} обновлён в базе данных!", newUser);
        return findUserById(newUser.getId());
    }

    @Override
    public User findUserById(Long userId) {
        try {
            return jdbcTemplate.queryForObject(FIND_USER_BY_ID_QUERY, userRowMapper, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с данным id " + userId + " не найден.");
        }
    }

    @Override
    public Collection<User> findAll() {
        return jdbcTemplate.query(FIND_ALL_USERS_QUERY, userRowMapper);
    }

    @Override
    public void deleteUser(Long userId) {
        int rowsAffected = jdbcTemplate.update(DELETE_USER, userId);
        if (rowsAffected == 0) {
            log.error("Пользователь с id {} не найден при удалении", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        log.debug("Пользователь с id {} удален из базы данных", userId);
    }

    public Set<Long> getFriends(Long userId) {
        return new HashSet<>(jdbcTemplate.queryForList(GET_FRIENDS, Long.class, userId, getStatusFriendshipId(StatusFriendship.CONFIRMED)));
    }

    private Integer getStatusFriendshipId(StatusFriendship status) {
        return jdbcTemplate.queryForObject(GET_STATUS_FRIENDSHIP, Integer.class, status.name());
    }

    public void addToFriends(Long userId, Long friendId) {
        jdbcTemplate.update(ADD_FRIEND, userId, friendId, getStatusFriendshipId(StatusFriendship.CONFIRMED));
    }

}