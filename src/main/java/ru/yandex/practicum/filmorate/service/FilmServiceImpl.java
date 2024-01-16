package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public Film findById(Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<Film> getAllFilm() {
        return filmStorage.findAll();
    }

    @Override
    public Film saveFilm(Film film) {
        return filmStorage.save(film);
    }

    @Override
    public Film updateFilm(Film film) {

        return filmStorage.update(film)
                .orElseThrow(() -> new NotFoundException("Фильм не найден", HttpStatus.NOT_FOUND));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        existIds(filmId, userId);
        filmStorage.addLike(filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(String count) {
        return filmStorage.getPopularFilm(Integer.valueOf(count));
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
}
