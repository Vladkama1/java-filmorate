package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.FilmDAO;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository(value = "filmDB")
@RequiredArgsConstructor
public class FilmDAOImpl implements FilmDAO {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Film> findById(Long id) {
        String sqlQuery = "SELECT f.*," +
                "       m.name               AS mpa_name," +
                "       GROUP_CONCAT(g.id)   AS genre_id," +
                "       GROUP_CONCAT(g.name) AS genre_name, " +
                "       GROUP_CONCAT(d.id)   AS director_id," +
                "       GROUP_CONCAT(d.name) AS director_name " +
                "FROM films AS f" +
                "         LEFT JOIN mpa AS m ON m.id = f.mpa_id" +
                "         LEFT JOIN films_genres AS fg ON f.id = fg.film_id" +
                "         LEFT JOIN genres AS g ON g.id = fg.genre_id " +
                "         LEFT JOIN films_directors AS fd ON f.id = fd.film_id" +
                "         LEFT JOIN directors AS d ON d.id = fd.director_id " +
                "WHERE f.id = ?" +
                "GROUP BY f.id";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilms, id);
        return films.stream().findFirst();
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "SELECT f.*," +
                "       m.name               AS mpa_name," +
                "       GROUP_CONCAT(g.id)   AS genre_id," +
                "       GROUP_CONCAT(g.name) AS genre_name, " +
                "       GROUP_CONCAT(d.id)   AS director_id," +
                "       GROUP_CONCAT(d.name) AS director_name " +
                "FROM films AS f" +
                "         LEFT JOIN mpa AS m ON m.id = f.mpa_id" +
                "         LEFT JOIN films_genres AS fg ON f.id = fg.film_id" +
                "         LEFT JOIN genres AS g ON g.id = fg.genre_id " +
                "         LEFT JOIN films_directors AS fd ON f.id = fd.film_id" +
                "         LEFT JOIN directors AS d ON d.id = fd.director_id " +
                "GROUP BY f.id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilms);
    }

    @Override
    public Film save(Film film) {
        String sqlQuery = "INSERT INTO films(name, release_date, description, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setDate(2, Date.valueOf(film.getReleaseDate()));
            stmt.setString(3, film.getDescription());
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId().intValue());
            return stmt;
        }, keyHolder);
        Long filmId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(filmId);
        updateFilmGenresLinks(film);
        updateFilmDirectorsLinks(film);
        return findById(filmId).orElse(null);
    }

    @Override
    public Optional<Film> update(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "name = ?,release_date = ?, description = ?, " +
                "duration = ?, mpa_id = ? " +
                "WHERE id = ?";
        int update = jdbcTemplate.update(sqlQuery, film.getName(), film.getReleaseDate(), film.getDescription(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        if (update == 0) {
            throw new NotFoundException("Фильм не найден!", HttpStatus.NOT_FOUND);
        }
        updateFilmGenresLinks(film);
        updateFilmDirectorsLinks(film);
        return findById(film.getId());
    }

    @Override
    public List<Film> getRecommendations(Long userId) {
        String sqlQuery = "SELECT f.*," +
                "       m.name               AS mpa_name," +
                "       GROUP_CONCAT(g.id)   AS genre_id," +
                "       GROUP_CONCAT(g.name) AS genre_name, " +
                "       GROUP_CONCAT(d.id)   AS director_id," +
                "       GROUP_CONCAT(d.name) AS director_name " +
                "FROM films AS f" +
                "         LEFT JOIN mpa AS m ON m.id = f.mpa_id" +
                "         LEFT JOIN films_genres AS fg ON f.id = fg.film_id" +
                "         LEFT JOIN genres AS g ON g.id = fg.genre_id " +
                "         LEFT JOIN films_directors AS fd ON f.id = fd.film_id" +
                "         LEFT JOIN directors AS d ON d.id = fd.director_id " +
                "WHERE f.id IN (SELECT film_id FROM films_users " +
                "WHERE user_id IN (SELECT user_id " +
                "    FROM (SELECT user_id,COUNT(user_id) AS crossing " +
                "    FROM (SELECT user_id FROM films_users " +
                "WHERE film_id IN(SELECT film_id FROM films_users WHERE user_id = ?) AND user_id != ?) " +
                "GROUP BY user_id) " +
                "WHERE crossing = (SELECT MAX(crossing) FROM " +
                "    (SELECT user_id,COUNT(user_id) AS crossing " +
                "     FROM (SELECT user_id FROM films_users " +
                "           WHERE film_id IN(SELECT film_id FROM films_users WHERE user_id = ?) " +
                "             AND user_id != ?) " +
                "     GROUP BY user_id))) AND film_id NOT IN (SELECT film_id FROM films_users WHERE user_id = ?) " +
                "    GROUP BY film_id)" +
                "    GROUP BY f.id";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilms, userId, userId, userId, userId, userId);
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO films_users (user_id, film_id)" +
                "VALUES ( ?,? ) ";
        try {
            jdbcTemplate.update(sqlQuery, userId, filmId);
            return true;
        } catch (DuplicateKeyException exception) {
            return false;
        }
    }

    @Override
    public boolean isExistById(Long id) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM films WHERE id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, id);
    }

    @Override
    public boolean delete(Long id) {
        String sqlQuery = "DELETE FROM films " +
                "WHERE id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM films_users " +
                "WHERE film_id = ? AND user_id = ?";
        return jdbcTemplate.update(sqlQuery, filmId, userId) > 0;
    }

    @Override
    public List<Film> getPopularFilm(Integer count, Integer genreId, Integer year) {
        String sqlQuery = "SELECT f.*," +
                "       m.name               AS mpa_name," +
                "       GROUP_CONCAT(g.id)   AS genre_id," +
                "       GROUP_CONCAT(g.name) AS genre_name," +
                "       GROUP_CONCAT(d.id)   AS director_id," +
                "       GROUP_CONCAT(d.name) AS director_name, " +
                "       COUNT(fu.user_id)    AS likes " +
                "FROM films AS f" +
                "         LEFT JOIN mpa AS m ON m.id = f.mpa_id" +
                "         LEFT JOIN films_genres AS fg ON f.id = fg.film_id" +
                "         LEFT JOIN genres AS g ON g.id = fg.genre_id" +
                "         LEFT JOIN films_users AS fu ON f.id = fu.film_id " +
                "         LEFT JOIN films_directors AS fd ON f.id = fd.film_id" +
                "         LEFT JOIN directors AS d ON d.id = fd.director_id " +
                "WHERE f.id IN (SELECT f.id" +
                "               FROM films AS f " +
                "                        LEFT JOIN films_genres AS fg ON f.id = fg.film_id " +
                "               WHERE (? IS NULL OR fg.genre_id = ?) " +
                "                 AND (? IS NULL OR YEAR(f.RELEASE_DATE) = ?)) " +
                "GROUP BY f.id " +
                "ORDER BY likes DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilms, genreId, genreId, year, year, count);
    }

    @Override
    public List<Film> findAllFilmsByDirectorId(Long directorId, String sortBy) {
        String sqlQuery = "SELECT f.*," +
                "       m.name               AS mpa_name," +
                "       GROUP_CONCAT(g.id)   AS genre_id," +
                "       GROUP_CONCAT(g.name) AS genre_name," +
                "       GROUP_CONCAT(d.id)   AS director_id," +
                "       GROUP_CONCAT(d.name) AS director_name, " +
                "       COUNT(fu.user_id)    AS likes " +
                "FROM films AS f" +
                "         LEFT JOIN mpa AS m ON m.id = f.mpa_id" +
                "         LEFT JOIN films_genres AS fg ON f.id = fg.film_id" +
                "         LEFT JOIN genres AS g ON g.id = fg.genre_id" +
                "         LEFT JOIN films_users AS fu ON f.id = fu.film_id " +
                "         LEFT JOIN films_directors AS fd ON f.id = fd.film_id" +
                "         LEFT JOIN directors AS d ON d.id = fd.director_id " +
                "WHERE d.id = ?" +
                "GROUP BY f.id " +
                "ORDER BY ";

        if (sortBy.equals("likes")) {
            sqlQuery += "likes";
        } else {
            sqlQuery += "f.release_date";
        }

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilms, directorId);
    }

    private Film mapRowToFilms(ResultSet resultSet, int rowNum) throws SQLException {
        return Film.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(mapRowToMpa(resultSet))
                .genres(mapRowToGenre(resultSet))
                .directors(mapRowToDirector(resultSet))
                .build();
    }

    private void updateFilmGenresLinks(Film film) {
        deleteFilmGenresLinks(film.getId());
        if (film.getGenres() == null) {
            return;
        }
        String sqlQuery = "INSERT INTO films_genres(film_id, genre_id) " +
                "VALUES (?, ?)";
        Set<Genre> genres = new TreeSet<>(Comparator.comparingLong(Genre::getId));
        genres.addAll(film.getGenres());
        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = new ArrayList<>(genres).get(i);
                ps.setLong(1, film.getId());
                ps.setLong(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    private void deleteFilmGenresLinks(Long filmId) {
        String sqlQuery = "DELETE FROM films_genres " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private void updateFilmDirectorsLinks(Film film) {
        deleteFilmDirectorsLinks(film.getId());
        if (film.getDirectors() == null) {
            return;
        }
        String sqlQuery = "INSERT INTO films_directors(film_id, director_id) " +
                "VALUES (?, ?)";
        Set<Director> directors = new TreeSet<>(Comparator.comparingLong(Director::getId));
        directors.addAll(film.getDirectors());
        jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Director director = new ArrayList<>(directors).get(i);
                ps.setLong(1, film.getId());
                ps.setLong(2, director.getId());
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        });
    }

    private void deleteFilmDirectorsLinks(Long filmId) {
        String sqlQuery = "DELETE FROM films_directors " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    private Set<Genre> mapRowToGenre(ResultSet resultSet) throws SQLException {
        Set<Genre> genreList = new HashSet<>();
        String genId = resultSet.getString("genre_id");
        String genName = resultSet.getString("genre_name");
        if (genId != null) {
            String[] genIds = genId.split(",");
            String[] genNames = genName.split(",");
            for (int i = 0; i < genIds.length; i++) {
                genreList.add(Genre.builder()
                        .id(Long.parseLong(genIds[i]))
                        .name(genNames[i])
                        .build());
            }
        }
        return genreList;
    }

    private Set<Director> mapRowToDirector(ResultSet resultSet) throws SQLException {
        Set<Director> directors = new HashSet<>();
        String directorId = resultSet.getString("director_id");
        String directorName = resultSet.getString("director_name");
        if (directorId != null) {
            String[] directorIds = directorId.split(",");
            String[] directorNames = directorName.split(",");
            for (int i = 0; i < directorIds.length; i++) {
                directors.add(Director.builder()
                        .id(Long.parseLong(directorIds[i]))
                        .name(directorNames[i])
                        .build());
            }
        }
        return directors;
    }

    private MPA mapRowToMpa(ResultSet resultSet) throws SQLException {
        return MPA.builder()
                .id(resultSet.getLong("mpa_id"))
                .name(resultSet.getString("mpa_name"))
                .build();
    }
}
