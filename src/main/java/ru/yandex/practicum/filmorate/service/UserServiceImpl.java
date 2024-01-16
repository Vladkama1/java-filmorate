package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage storage;

    @Override
    public User findById(Long id) {
        return storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<User> getAllUsers() {
        return storage.findAll();
    }

    @Override
    public List<User> getAllFriends(Long id) {
        return storage.getAllFriends(id);
    }

    @Override
    public List<User> getAllMutualFriends(Long id, Long otherId) {
        return storage.getAllMutualFriends(id, otherId);
    }

    @Override
    public User saveUser(User user) {
        return storage.save(user);
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        existUser(id, friendId);
        storage.addFriend(id, friendId);
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        existUser(id, friendId);
        storage.delete(id, friendId);
    }

    @Override
    public User updateUser(User user) {
        return storage.update(user)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND));
    }

    private void existUser(Long id, Long otherId) {
        String userNotFound = "User not found by ID: ";
        boolean isExistUser = storage.isExistById(id);
        boolean isExistOtherUser = storage.isExistById(otherId);
        if (!isExistUser && !isExistOtherUser) {
            throw new NotFoundException(userNotFound + id + " " + userNotFound + otherId, HttpStatus.NOT_FOUND);
        } else if (!isExistUser) {
            throw new NotFoundException(userNotFound + id, HttpStatus.NOT_FOUND);
        } else if (!isExistOtherUser) {
            throw new NotFoundException(userNotFound + otherId, HttpStatus.NOT_FOUND);
        }
    }
}
