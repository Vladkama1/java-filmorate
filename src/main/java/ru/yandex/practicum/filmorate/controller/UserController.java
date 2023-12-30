package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private int id = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAll() {
        log.info("Получен запрос Get.");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос Post.");
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(createId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь {}", user.getName());
        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        log.info("Получен запрос Put.");
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Film this is not ID", HttpStatus.NOT_FOUND);
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Обновлён пользователь {}", user.getLogin());
        return user;
    }

    private int createId() {
        return id++;
    }
}
