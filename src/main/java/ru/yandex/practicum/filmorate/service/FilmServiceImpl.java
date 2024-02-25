package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.FilmDAO;
import ru.yandex.practicum.filmorate.storage.UserDAO;

import java.util.List;

import static ru.yandex.practicum.filmorate.constants.FilmConstant.FILM_RELEASE;

@Service
public class FilmServiceImpl implements FilmService {
    private final FilmDAO filmDAO;
    private final UserDAO userDAO;
    private final FilmMapper mapper;

    @Autowired
    public FilmServiceImpl(@Qualifier(value = "filmDB") FilmDAO filmDAO,
                           @Qualifier(value = "userDB") UserDAO userDAO,
                           FilmMapper mapper) {
        this.filmDAO = filmDAO;
        this.userDAO = userDAO;
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
        validatedReleaseFilm(filmDTO);
        return mapper.toDTO(filmDAO.save(mapper.toModel(filmDTO)));
    }

    @Override
    public FilmDTO updateFilm(FilmDTO filmDTO) {
        validatedReleaseFilm(filmDTO);
        return mapper.toDTO(filmDAO.update(mapper.toModel(filmDTO))
                .orElseThrow(() -> new NotFoundException("Фильм не найден", HttpStatus.NOT_FOUND)));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        existIds(filmId, userId);
        filmDAO.addLike(filmId, userId);
    }

    @Override
    public List<FilmDTO> getPopularFilms(String count) {
        return mapper.toListDTO(filmDAO.getPopularFilm(Integer.valueOf(count)));
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        existIds(id, userId);
        filmDAO.deleteLike(id, userId);
    }

    @Override
    public void delete(Long id) {
        filmDAO.delete(id);
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

    private void validatedReleaseFilm(FilmDTO filmDTO) {
        if (filmDTO.getReleaseDate().isBefore(FILM_RELEASE)) {
            throw new ValidException("Data release before 28.12.1895 year", HttpStatus.BAD_REQUEST);
        }
    }
}
