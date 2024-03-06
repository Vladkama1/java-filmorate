package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.anotation.MarkerOfCreate;
import ru.yandex.practicum.filmorate.anotation.MarkerOfUpdate;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @GetMapping("/{id}")
    public UserDTO findById(@PathVariable Long id) {
        log.info("Получен запрос GET, на получения пользователя, по id: {}", id);
        return service.findById(id);
    }

    @GetMapping("/{id}")
    public List<UserDTO> getRecommendations() {
        return null;
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        log.info("Получен запрос GET, на получения всех пользователей.");
        List<UserDTO> userDTOList = service.getAllUsers();
        log.info("Получен ответ, список пользователей, размер: {}", userDTOList.size());
        return userDTOList;
    }

    @GetMapping("/{id}/friends")
    public List<UserDTO> getAllFriends(@PathVariable Long id) {
        log.info("Получен запрос GET, на получение всех друзей, по id: {}", id);
        List<UserDTO> userDTOList = service.getAllFriends(id);
        log.info("Получен список всех друзей {}, пользователя.", userDTOList.size());
        return userDTOList;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDTO> getAllMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен запрос GET, на получение общих друзе.");
        List<UserDTO> userDTOList = service.getAllMutualFriends(id, otherId);
        log.info("Получен список общих друзей: {}", userDTOList.size());
        return userDTOList;
    }

    @PostMapping
    @Validated({MarkerOfCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public UserDTO saveUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("Получен запрос Post, на обновление данных пользователя.");
        log.info("Добавлен пользователь: {}", userDTO.getName());
        return service.saveUser(userDTO);
    }

    @PutMapping
    @Validated({MarkerOfUpdate.class})
    public UserDTO updateUser(@Valid @RequestBody UserDTO userDTO) {
        log.info("Получен запрос Put, на обновление пользователя");
        UserDTO userDTO1 = service.updateUser(userDTO);
        log.info("Обновлён пользователь: {}", userDTO1.getLogin());
        return userDTO1;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос PUT, на обновления списка друзей по id: {}", id);
        service.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Получен запрос DELETE, на удаления пользователя, по id: {}", id);
        service.delete(id);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен запрос DELETE, удаление из друзей по id: {}", id);
        service.deleteFriend(id, friendId);
    }
}
