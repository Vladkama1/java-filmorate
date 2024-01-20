package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO findById(Long id);

    List<UserDTO> getAllUsers();

    List<UserDTO> getAllFriends(Long id);

    List<UserDTO> getAllMutualFriends(Long id, Long otherId);

    UserDTO saveUser(UserDTO userDTO);

    void addFriend(Long id, Long friendId);

    void deleteFriend(Long id, Long friendId);

    UserDTO updateUser(UserDTO userDTO);
}
