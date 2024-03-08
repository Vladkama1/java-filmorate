package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.EventDao;
import ru.yandex.practicum.filmorate.storage.UserDAO;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserDAO storage;
    private final UserMapper mapper;

    private final EventDao eventDao;

    @Autowired
    public UserServiceImpl(@Qualifier(value = "userDB") UserDAO storage, EventDao eventDao, UserMapper mapper) {
        this.storage = storage;
        this.eventDao = eventDao;
        this.mapper = mapper;
    }

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
        boolean existById = storage.isExistById(id);
        if (!existById) {
            throw new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND);
        }
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
        boolean addedFriend = storage.addFriend(id, friendId);
        if (!addedFriend) {
            throw new ValidException("Дружба уже существует!", HttpStatus.BAD_REQUEST);
        }
        // Запись в лог действий
        eventDao.save(Event.builder()
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .userId(id)
                .entityId(friendId).build());
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        existUser(id, friendId);
        storage.deleteFriend(id, friendId);
        // Запись в лог действий
        eventDao.save(Event.builder()
                .eventType(EventType.FRIEND)
                .operation(Operation.REMOVE)
                .userId(id)
                .entityId(friendId).build());
    }

    @Override
    public void delete(Long id) {
        boolean deleted = storage.delete(id);
        if (!deleted) {
            throw new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        validatedUserName(userDTO);
        return mapper.toDTO(storage.update(mapper.toModel(userDTO))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND)));
    }

    public List<EventDto> getFeed(Long id) {
        boolean existById = storage.isExistById(id);
        if (!existById) {
            throw new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND);
        }

        return eventDao.getFriendsFeed(id).stream().map(Event::toDto).collect(Collectors.toList());
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
