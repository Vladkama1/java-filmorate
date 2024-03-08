package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapperImpl;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserDAO;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    private UserMapper mapper = new UserMapperImpl();
    private UserDAO userDAO = new InMemoryUserStorage();
    private UserService userService = new UserServiceImpl(userDAO, null,mapper);
    private UserController userController;

    UserDTO user = UserDTO.builder()
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
    @DisplayName("Проверка замены имени на логин!")
    void nameNullLogin() {
        user.setName("");
        userController.saveUser(user);
        assertEquals(user.getName(), user.getLogin());
    }
}