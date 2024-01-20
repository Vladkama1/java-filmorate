package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.constants.FilmConstant.FILM_RELEASE;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmMapper mapper;

    @Override
    public FilmDTO findById(Long id) {
        return mapper.toDTO(filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден", HttpStatus.NOT_FOUND)));
    }

    @Override
    public List<FilmDTO> getAllFilm() {
        return mapper.toListDTO(filmStorage.findAll());
    }

    @Override
    public FilmDTO saveFilm(FilmDTO filmDTO) {
        validatedReleaseFilm(filmDTO);
        return mapper.toDTO(filmStorage.save(mapper.toModel(filmDTO)));
    }

    @Override
    public FilmDTO updateFilm(FilmDTO filmDTO) {
        validatedReleaseFilm(filmDTO);
        return mapper.toDTO(filmStorage.update(mapper.toModel(filmDTO))
                .orElseThrow(() -> new NotFoundException("Фильм не найден", HttpStatus.NOT_FOUND)));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        existIds(filmId, userId);
        filmStorage.addLike(filmId, userId);
    }

    @Override
    public List<FilmDTO> getPopularFilms(String count) {
        return mapper.toListDTO(filmStorage.getPopularFilm(Integer.valueOf(count)));
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        existIds(id, userId);
        filmStorage.deleteLike(id, userId);
    }

    @Override
    public void delete(Long id) {
        filmStorage.delete(id);
    }

    private void existIds(Long filmId, Long userId) {
        String filmNotFound = "Film not found by ID: ";
        String userNotFound = "User not found by ID: ";
        boolean isExistFilm = filmStorage.isExistById(filmId);
        boolean isExistUser = userStorage.isExistById(userId);
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
