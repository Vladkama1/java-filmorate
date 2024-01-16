package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> findById(Long id);

    List<Film> findAll();

    Film save(Film film);

    Optional<Film> update(Film film);

    boolean addLike(Long filmId, Long userId);

    boolean isExistById(Long id);

    boolean delete(Long id);

    boolean deleteLike(Long filmId, Long userId);

    List<Film> getPopularFilm(Integer count);
}