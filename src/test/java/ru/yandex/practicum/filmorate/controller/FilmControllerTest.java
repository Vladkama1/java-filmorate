package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.exceptions.ValidException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapperImpl;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    private FilmMapper mapper = new FilmMapperImpl();
    private FilmStorage filmStorage = new InMemoryFilmStorage();
    private UserStorage userStorage = new InMemoryUserStorage();
    private FilmService filmService = new FilmServiceImpl(filmStorage, userStorage, mapper);
    private FilmController filmController;
    FilmDTO film = FilmDTO.builder()
            .name("film name")
            .description("film description")
            .releaseDate(LocalDate.of(1997, 3, 24))
            .duration(100)
            .build();

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController(filmService);
    }

    @Test
    void createFailDate() {
        film.setReleaseDate(LocalDate.of(1795, 12, 28));
        Throwable exception = assertThrows(ValidException.class, () -> filmController.createFilm(film));
        assertEquals(exception.getMessage(), "Data release before 28.12.1895 year");
    }
}