package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage storage;
    private final UserMapper mapper;

    @Override
    public UserDTO findById(Long id) {
        return mapper.toDTO(storage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND)));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return mapper.toListDTO(storage.findAll());
    }

    @Override
    public List<UserDTO> getAllFriends(Long id) {
        return mapper.toListDTO(storage.getAllFriends(id));
    }

    @Override
    public List<UserDTO> getAllMutualFriends(Long id, Long otherId) {
        return mapper.toListDTO(storage.getAllMutualFriends(id, otherId));
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        validatedUserName(userDTO);
        return mapper.toDTO(storage.save(mapper.toModel(userDTO)));
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
    public UserDTO updateUser(UserDTO userDTO) {
        validatedUserName(userDTO);
        return mapper.toDTO(storage.update(mapper.toModel(userDTO))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND)));
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

    private void validatedUserName(UserDTO userDTO) {
        if (userDTO.getName() == null || userDTO.getName().isEmpty()) {
            userDTO.setName(userDTO.getLogin());
            log.warn("Имя отсутствует, поэтому имя заменено на логин: {}!", userDTO.getLogin());
        }
    }
}
