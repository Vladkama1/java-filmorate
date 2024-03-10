package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Repository(value = "filmMemory")
public class InMemoryFilmStorage implements FilmDAO {
    private Long id = 1L;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> getRecommendations(Long userId) {
        return null;
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film save(Film film) {
        film.setId(createIdFilm());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        if (!films.containsKey(film.getId())) {
            return Optional.empty();
        }
        films.put(film.getId(), film);
        return Optional.of(film);
    }


    @Override
    public boolean delete(Long id) {
        Film film = films.remove(id);
        if (film == null) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteLike(Long id, Long userId) {
        return films.get(id).getLikes().remove(userId);
    }

    @Override
    public List<Film> getPopularFilm(Integer count, Long genreId, Integer year) {
        return findAll()
                .stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> findAllFilmsByDirectorId(Long directorId, String sortBy) {
        return null;
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        return null;
    }

    @Override
    public List<Film> getAllMutualFilms(Long userId, Long friendId) {
        return null;
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        return films.get(filmId).getLikes().add(userId);
    }

    @Override
    public boolean isExistById(Long id) {
        return films.containsKey(id);
    }

    private Long createIdFilm() {
        return id++;
    }
}
