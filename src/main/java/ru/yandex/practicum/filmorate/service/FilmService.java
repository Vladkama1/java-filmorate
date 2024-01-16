package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film findById(Long id);

    List<Film> getAllFilm();

    List<Film> getPopularFilms(String count);

    Film saveFilm(Film film);

    Film updateFilm(Film film);

    void deleteLike(Long id, Long userId);

    void delete(Long id);

    void addLike(Long filmId, Long userId);
}
