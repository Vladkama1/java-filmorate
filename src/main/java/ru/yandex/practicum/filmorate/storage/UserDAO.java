package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    User save(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    Optional<User> update(User user);

    boolean deleteFriend(Long id, Long friendId);

    boolean isExistById(Long id);

    boolean addFriend(Long id, Long friendId);

    List<User> getAllFriends(Long id);

    List<User> getAllMutualFriends(Long id, Long otherId);
}
