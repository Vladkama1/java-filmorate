package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidException;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.EventDao;
import ru.yandex.practicum.filmorate.storage.FilmDAO;
import ru.yandex.practicum.filmorate.storage.UserDAO;

import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private final FilmDAO filmDAO;
    private final UserMapper userMapper;
    private final FilmMapper filmMapper;
    private final EventMapper eventMapper;
    private final EventDao eventDao;

    @Autowired
    public UserServiceImpl(@Qualifier(value = "userDB") UserDAO storage, @Qualifier(value = "filmDB") FilmDAO filmDAO,
                           EventDao eventDao, UserMapper mapper, FilmMapper filmMapper, EventMapper eventMapper) {
        this.userDAO = storage;
        this.filmDAO = filmDAO;
        this.eventDao = eventDao;
        this.userMapper = mapper;
        this.filmMapper = filmMapper;
        this.eventMapper = eventMapper;
    }

    @Override
    public UserDTO findById(Long id) {
        return userMapper.toDTO(userDAO.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND)));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userMapper.toListDTO(userDAO.findAll());
    }

    @Override
    public List<FilmDTO> getRecommendations(Long userId) {
        return filmMapper.toListDTO(filmDAO.getRecommendations(userId));
    }

    @Override
    public List<UserDTO> getAllFriends(Long id) {
        boolean existById = userDAO.isExistById(id);
        if (!existById) {
            throw new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND);
        }
        return userMapper.toListDTO(userDAO.getAllFriends(id));
    }

    @Override
    public List<UserDTO> getAllMutualFriends(Long id, Long otherId) {
        return userMapper.toListDTO(userDAO.getAllMutualFriends(id, otherId));
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        validatedUserName(userDTO);
        return userMapper.toDTO(userDAO.save(userMapper.toModel(userDTO)));
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        existUser(id, friendId);
        boolean addedFriend = userDAO.addFriend(id, friendId);
        if (!addedFriend) {
            throw new ValidException("Дружба уже существует!", HttpStatus.BAD_REQUEST);
        }
        eventDao.save(Event.builder()
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .userId(id)
                .entityId(friendId).build());
    }

    @Override
    public void deleteFriend(Long id, Long friendId) {
        existUser(id, friendId);
        boolean deleteFriend = userDAO.deleteFriend(id, friendId);
        if (!deleteFriend) {
            throw new ValidException("Дружбы не существует!", HttpStatus.BAD_REQUEST);
        }
        eventDao.save(Event.builder()
                .eventType(EventType.FRIEND)
                .operation(Operation.REMOVE)
                .userId(id)
                .entityId(friendId).build());
    }

    @Override
    public void delete(Long id) {
        boolean deleted = userDAO.delete(id);
        if (!deleted) {
            throw new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        validatedUserName(userDTO);
        return userMapper.toDTO(userDAO.update(userMapper.toModel(userDTO))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND)));
    }

    @Override
    public List<EventDto> getFeed(Long id) {
        boolean existById = userDAO.isExistById(id);
        if (!existById) {
            throw new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND);
        }
        return eventMapper.toListDTO(eventDao.getFriendsFeed(id));
    }

    private void existUser(Long id, Long otherId) {
        String userNotFound = "User not found by ID: ";
        boolean isExistUser = userDAO.isExistById(id);
        boolean isExistOtherUser = userDAO.isExistById(otherId);
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
