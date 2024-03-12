package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorDAO {
    Optional<Director> findById(Long id);

    List<Director> findAll();

    Director save(Director film);

    Optional<Director> update(Director film);

    boolean isExistById(Long id);

    boolean delete(Long id);
}
