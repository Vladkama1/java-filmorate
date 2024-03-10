package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.List;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private final FilmDAO filmDAO;
    private final UserDAO userDAO;
    private final DirectorDAO directorDAO;
    private final EventDao eventDao;
    private final GenreDAO genreDAO;
    private final FilmMapper mapper;

    @Autowired
    public FilmServiceImpl(@Qualifier(value = "filmDB") FilmDAO filmDAO,
                           @Qualifier(value = "userDB") UserDAO userDAO,
                           DirectorDAO directorDAO,
                           GenreDAO genreDAO,
                           EventDao eventDao,
                           FilmMapper mapper) {
        this.filmDAO = filmDAO;
        this.userDAO = userDAO;
        this.directorDAO = directorDAO;
        this.eventDao = eventDao;
        this.genreDAO = genreDAO;
        this.mapper = mapper;
    }

    @Override
    public FilmDTO findById(Long id) {
        return mapper.toDTO(filmDAO.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден", HttpStatus.NOT_FOUND)));
    }

    @Override
    public List<FilmDTO> getAllFilm() {
        return mapper.toListDTO(filmDAO.findAll());
    }

    @Override
    public FilmDTO saveFilm(FilmDTO filmDTO) {
        return mapper.toDTO(filmDAO.save(mapper.toModel(filmDTO)));
    }

    @Override
    public FilmDTO updateFilm(FilmDTO filmDTO) {
        return mapper.toDTO(filmDAO.update(mapper.toModel(filmDTO))
                .orElseThrow(() -> new NotFoundException("Фильм не найден", HttpStatus.NOT_FOUND)));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        existIds(filmId, userId);
        filmDAO.addLike(filmId, userId);
        eventDao.save(Event.builder()
                .eventType(EventType.LIKE)
                .operation(Operation.ADD)
                .userId(userId)
                .entityId(filmId).build());
    }

    @Override
    public List<FilmDTO> getPopularFilms(Integer count, Long genreId, Integer year) {
        if (genreId != null) {
            boolean isExistGenre = genreDAO.isExistById(genreId);
            if (!isExistGenre) {
                throw new NotFoundException("Genre not found by ID: " + genreId, HttpStatus.NOT_FOUND);
            }
        }
        return mapper.toListDTO(filmDAO.getPopularFilm(count, genreId, year));
    }

    @Override
    public List<FilmDTO> getFilmsByDirectorId(Long directorId, String sortBy) {
        boolean isExistDirector = directorDAO.isExistById(directorId);
        if (!isExistDirector) {
            throw new NotFoundException("Director not found by ID: " + directorId, HttpStatus.NOT_FOUND);
        }
        return mapper.toListDTO(filmDAO.findAllFilmsByDirectorId(directorId, sortBy));
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        existIds(id, userId);
        filmDAO.deleteLike(id, userId);
        eventDao.save(Event.builder()
                .eventType(EventType.LIKE)
                .operation(Operation.REMOVE)
                .userId(userId)
                .entityId(id).build());
    }

    @Override
    public void delete(Long id) {
        boolean deleted = filmDAO.delete(id);
        if (!deleted) {
            throw new NotFoundException("Фильм не найден", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public List<FilmDTO> searchFilms(String query, String by) {
        if (!(by.contains("title") || by.contains("director") || by.contains("title,director") || by.contains("director,title") || by.contains("unknown"))) {
            log.info("Некорректное значение выборки поиска в поле BY = {}", by);
            throw new IllegalArgumentException("Некорректное значение выборки поиска");
        }
        return mapper.toListDTO(filmDAO.searchFilms(query, by));
    }

    @Override
    public List<FilmDTO> getAllMutualFilms(Long userId, Long friendId) {
        existUser(userId);
        existUser(friendId);
        return mapper.toListDTO(filmDAO.getAllMutualFilms(userId, friendId));
    }

    private void existUser(Long userId) {
        boolean isExistUser = userDAO.isExistById(userId);
        if (!isExistUser) {
            throw new NotFoundException("User not found by ID: " + userId, HttpStatus.NOT_FOUND);
        }
    }

    private void existIds(Long filmId, Long userId) {
        String filmNotFound = "Film not found by ID: ";
        String userNotFound = "User not found by ID: ";
        boolean isExistFilm = filmDAO.isExistById(filmId);
        boolean isExistUser = userDAO.isExistById(userId);
        if (!isExistUser && !isExistFilm) {
            throw new NotFoundException(filmNotFound + filmId + " " + userNotFound + userId, HttpStatus.NOT_FOUND);
        } else if (!isExistFilm) {
            throw new NotFoundException(filmNotFound + filmId, HttpStatus.NOT_FOUND);
        } else if (!isExistUser) {
            throw new NotFoundException(userNotFound + userId, HttpStatus.NOT_FOUND);
        }
    }
}
