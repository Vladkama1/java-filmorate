package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    UserController userController;

    User user = User.builder()
            .name("user name")
            .email("lovt.vlad@mail.ru")
            .login("kama")
            .birthday(LocalDate.of(1997, 3, 24))
            .build();

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
    }

    @Test
    void nameNullLogin() {
        user.setName("");
        userController.create(user);
        assertEquals(user.getLogin(), user.getLogin());
    }
}