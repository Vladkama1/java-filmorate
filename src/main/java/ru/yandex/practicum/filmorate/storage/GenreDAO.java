package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreDAO {
    Optional<Genre> findById(Long id);

    List<Genre> findAll();

    boolean isExistById(Long id);
}
