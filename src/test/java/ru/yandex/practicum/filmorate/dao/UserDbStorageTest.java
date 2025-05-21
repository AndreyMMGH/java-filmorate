package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    public void testShouldGetUserById() {
        User user = new User();
        user.setEmail("User@mail.ru");
        user.setLogin("User");
        user.setName("Григорий");
        user.setBirthday(LocalDate.of(1998, 1, 20));

        User createdUser = userStorage.createUser(user);

        User user2 = userStorage.findUserById(createdUser.getId());

        assertThat(user2).isNotNull();
        assertThat(user2.getId()).isEqualTo(createdUser.getId());
    }

    @Test
    public void testShouldCreateUser() {
        User user = new User();

        user.setEmail("User@mail.ru");
        user.setLogin("User");
        user.setName("Григорий");
        user.setBirthday(LocalDate.of(1998, 1, 20));

        User user1 = userStorage.createUser(user);

        assertThat(user1)
                .hasFieldOrPropertyWithValue("email", "User@mail.ru");
    }

    @Test
    public void testShouldUpdateUser() {
        User user = new User();

        user.setEmail("User@mail.ru");
        user.setLogin("User");
        user.setName("Григорий");
        user.setBirthday(LocalDate.of(1998, 1, 20));

        userStorage.createUser(user);

        User userUpdated = new User();

        userUpdated.setId(user.getId());
        userUpdated.setEmail("User2@mail.ru");
        userUpdated.setLogin("User2");
        userUpdated.setName("Григорий2");
        userUpdated.setBirthday(LocalDate.of(1995, 12, 5));

        userStorage.updateUser(userUpdated);

        assertThat(userUpdated)
                .hasFieldOrPropertyWithValue("email", "User2@mail.ru")
                .hasFieldOrPropertyWithValue("login", "User2")
                .hasFieldOrPropertyWithValue("name", "Григорий2")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(1995, 12, 5));
    }

    @Test
    public void testShouldFindAll() {
        User user1 = new User();
        user1.setEmail("User@mail.ru");
        user1.setLogin("User");
        user1.setName("Григорий");
        user1.setBirthday(LocalDate.of(1998, 1, 20));
        User newUser1 = userStorage.createUser(user1);

        User user2 = new User();
        user2.setEmail("User2@mail.ru");
        user2.setLogin("User2");
        user2.setName("Григорий2");
        user2.setBirthday(LocalDate.of(1999, 2, 21));
        User newUser2 = userStorage.createUser(user2);

        Collection<User> allUsers = userStorage.findAll();

        assertThat(allUsers)
                .hasSize(2)
                .containsExactlyInAnyOrder(newUser1, newUser2);
    }

    @Test
    public void testShouldDeleteUser() {

        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("user");
        user.setName("Григорий");
        user.setBirthday(LocalDate.of(2000, 4, 1));
        user.setFriends(Set.of(2L, 3L));

        userStorage.createUser(user);
        Long id = user.getId();
        userStorage.deleteUser(id);

        assertNull(userStorage.findUserById(id), "Пользователь не удален");
    }

    @Test
    public void testShouldAddToFriend() {
        User user1 = new User();
        user1.setEmail("user@mail.ru");
        user1.setLogin("user");
        user1.setName("Григорий");
        user1.setBirthday(LocalDate.of(2000, 4, 1));
        user1.setFriends(Set.of(2L, 3L));

        User user2 = new User();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2");
        user2.setName("Григорий2");
        user2.setBirthday(LocalDate.of(1998, 3, 2));
        user2.setFriends(Set.of(1L));

        userStorage.createUser(user1);
        userStorage.createUser(user2);

        Long userId = user1.getId();
        Long friendId = user2.getId();

        userStorage.addToFriend(user1, user2);

        User addedUser = userStorage.findUserById(userId);
        User addedUser2 = userStorage.findUserById(friendId);

        assertEquals(1, addedUser.getFriends().size(), "Друг должен быть 1");
        assertEquals(0, addedUser2.getFriends().size(), "Дружба должна быть односторонней");
    }

    @Test
    void testShouldRemoveFromFriends() {
        User user1 = new User();
        user1.setEmail("user@mail.ru");
        user1.setLogin("user");
        user1.setName("Григорий");
        user1.setBirthday(LocalDate.of(2000, 4, 1));
        user1.setFriends(Set.of(2L, 3L));

        User user2 = new User();
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2");
        user2.setName("Григорий2");
        user2.setBirthday(LocalDate.of(1998, 3, 2));
        user2.setFriends(Set.of(1L));


        userStorage.createUser(user1);
        userStorage.createUser(user2);

        Long userId = user1.getId();
        Long friendId = user2.getId();

        userStorage.addToFriend(user1, user2);
        userStorage.addToFriend(user2, user1);

        userStorage.removeFromFriends(userId, friendId);

        User removedUser = userStorage.findUserById(userId);
        User removedUser2 = userStorage.findUserById(friendId);

        assertEquals(0, removedUser.getFriends().size(), "друг должен быть удален");
        assertEquals(1, removedUser2.getFriends().size(), "у друга список друзей должен остаться прежним");
    }
}

