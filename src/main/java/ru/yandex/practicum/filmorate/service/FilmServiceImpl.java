package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.DirectorDAO;
import ru.yandex.practicum.filmorate.storage.FilmDAO;
import ru.yandex.practicum.filmorate.storage.UserDAO;

import java.util.List;

@Slf4j
@Service
public class FilmServiceImpl implements FilmService {
    private final FilmDAO filmDAO;
    private final UserDAO userDAO;
    private final DirectorDAO directorDAO;
    private final FilmMapper mapper;

    @Autowired
    public FilmServiceImpl(@Qualifier(value = "filmDB") FilmDAO filmDAO,
                           @Qualifier(value = "userDB") UserDAO userDAO,
                           DirectorDAO directorDAO,
                           FilmMapper mapper) {
        this.filmDAO = filmDAO;
        this.userDAO = userDAO;
        this.directorDAO = directorDAO;
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
    }

    @Override
    public List<FilmDTO> getPopularFilms(Integer count, Integer genreId, Integer year) {
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
