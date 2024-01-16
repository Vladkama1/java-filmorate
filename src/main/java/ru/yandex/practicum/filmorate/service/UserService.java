package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    User findById(Long id);

    List<User> getAllUsers();

    List<User> getAllFriends(Long id);

    List<User> getAllMutualFriends(Long id, Long otherId);

    User saveUser(User user);

    void addFriend(Long id, Long friendId);

    void deleteFriend(Long id, Long friendId);

    User updateUser(User user);
}
