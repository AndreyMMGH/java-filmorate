package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    UserController userController = new UserController();
    User user;

    @BeforeEach
    public void testUser() {
        user = new User(1L, "NewEmail@gmail.com", "Evg90", "Евгений", LocalDate.parse("1990-10-15"));
    }

    @Test
    public void checkTheEmailFieldOnNull() {
        user.setEmail(null);
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", assertThrows(ValidationException.class, () -> userController.createUser(user)).getMessage());
    }

    @Test
    public void checkTheEmailFieldOnIsEmpty() {
        user.setEmail("       ");
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", assertThrows(ValidationException.class, () -> userController.createUser(user)).getMessage());
    }

    @Test
    public void checkTheEmailFieldForTheSign() {
        user.setEmail("NewEmailgmail.com");
        assertEquals("Электронная почта не может быть пустой и должна содержать символ @", assertThrows(ValidationException.class, () -> userController.createUser(user)).getMessage());
    }

    @Test
    public void checkTheLoginFieldOnNull() {
        user.setLogin(null);
        assertEquals("Логин не может быть пустым и содержать пробелы", assertThrows(ValidationException.class, () -> userController.createUser(user)).getMessage());
    }

    @Test
    public void checkTheLoginFieldIsEmpty() {
        user.setLogin("       ");
        assertEquals("Логин не может быть пустым и содержать пробелы", assertThrows(ValidationException.class, () -> userController.createUser(user)).getMessage());
    }

    @Test
    public void checkIfTheEmptyFieldIsAssignedALogin() {
        user.setName(null);
        userController.createUser(user);
        assertEquals(user.getLogin(), user.getName(), "При отсутствующем наименовании должен присваиваться логин");
    }

    @Test
    public void checkingDateOfBirthForTheFuture() {
        user.setBirthday(LocalDate.parse("2025-10-15"));
        assertEquals("Дата рождения не может быть в будущем.", assertThrows(ValidationException.class, () -> userController.createUser(user)).getMessage());
    }

    @Test
    public void checkingTheIdField() {
        user.setId(2L);
        assertEquals("Пользователь с данным id - " + user.getId() + " не найден", assertThrows(NotFoundException.class, () -> userController.updateUser(user)).getMessage());
    }

    @Test
    public void checkingTheIdFieldOnNull() {
        user.setId(null);
        assertEquals("Id пользователя должен быть указан", assertThrows(ValidationException.class, () -> userController.updateUser(user)).getMessage());
    }
}