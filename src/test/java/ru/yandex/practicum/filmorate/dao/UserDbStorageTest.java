package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class})
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    public void testGetUserById() {
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
    public void testCreateUser() {
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
    public void testUpdateUser() {
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
    public void testFindAll() {
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
    public void testDeleteUser() {
        User user1 = new User();
        user1.setEmail("User@mail.ru");
        user1.setLogin("User");
        user1.setName("Григорий");
        user1.setBirthday(LocalDate.of(1998, 1, 20));
        User newUser = userStorage.createUser(user1);

        Optional<User> beforeDelete = Optional.ofNullable(userStorage.findUserById(newUser.getId()));
        assertThat(beforeDelete).isPresent();

        assertDoesNotThrow(() -> userStorage.deleteUser(newUser.getId()));

        assertThatThrownBy(() -> userStorage.findUserById(newUser.getId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("не найден");
    }

}

