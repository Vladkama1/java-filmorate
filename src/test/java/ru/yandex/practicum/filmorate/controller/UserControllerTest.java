package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    private UserStorage userStorage = new InMemoryUserStorage();
    private UserService userService = new UserServiceImpl(userStorage);
    private UserController userController;

    User user = User.builder()
            .name("user name")
            .email("lovt.vlad@mail.ru")
            .login("kama")
            .birthday(LocalDate.of(1997, 3, 24))
            .build();

    @BeforeEach
    void beforeEach() {
        userController = new UserController(userService);
    }

    @Test
    void nameNullLogin() {
        user.setName("");
        userController.saveUser(user);
        assertEquals(user.getLogin(), user.getLogin());
    }
}