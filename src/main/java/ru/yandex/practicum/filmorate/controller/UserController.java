package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Получен запрос Get.");
        return service.getAllUsers();
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable Long id) {
        log.info("Получен запрос Get.");
        return service.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getAllMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен запрос Get.");
        return service.getAllMutualFriends(id, otherId);
    }

    @PostMapping
    public User saveUser(@Valid @RequestBody User user) {
        log.info("Получен запрос Post.");
        validatedUserName(user);
        log.info("Добавлен пользователь: {}", user.getName());
        return service.saveUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Получен запрос Put.");
        validatedUserName(user);
        log.info("Обновлён пользователь: {}", user.getLogin());
        return service.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        service.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        service.deleteFriend(id, friendId);
    }

    private void validatedUserName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
            log.warn("Имя отсутствует, поэтому имя заменено на логин: {}!", user.getLogin());
        }
    }
}
