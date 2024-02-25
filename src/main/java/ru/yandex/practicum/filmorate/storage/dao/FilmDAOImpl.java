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
        String sqlQuery = "SELECT F.*," +
                "       M.name               as mpa_name," +
                "       GROUP_CONCAT(G.id)   as genre_id," +
                "       GROUP_CONCAT(G.name) as genre_name " +
                "FROM FILMS AS F" +
                "         LEFT JOIN PUBLIC.MPA M on M.ID = F.MPA_ID" +
                "         LEFT JOIN PUBLIC.FILMS_GENRES FG on F.ID = FG.FILM_ID" +
                "         LEFT JOIN PUBLIC.GENRES G on G.ID = FG.GENRE_ID " +
                "WHERE F.ID = ?" +
                "GROUP BY F.ID";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilms, id);
        return films.stream().findFirst();
    }

    @Override
    public List<Film> findAll() {
        String sqlQuery = "SELECT F.*," +
                "       M.name               as mpa_name," +
                "       GROUP_CONCAT(G.id)   as genre_id," +
                "       GROUP_CONCAT(G.name) as genre_name " +
                "FROM FILMS AS F" +
                "         LEFT JOIN PUBLIC.MPA M on M.ID = F.MPA_ID" +
                "         LEFT JOIN PUBLIC.FILMS_GENRES FG on F.ID = FG.FILM_ID" +
                "         LEFT JOIN PUBLIC.GENRES G on G.ID = FG.GENRE_ID " +
                "GROUP BY F.ID";
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
        return findById(filmId).orElse(null);
    }

    @Override
    public Optional<Film> update(Film film) {
        String sqlQuery = "UPDATE FILMS SET " +
                "name = ?,release_date = ?, description = ?, " +
                "duration = ?, mpa_id = ? " +
                "WHERE id = ?";
        int update = jdbcTemplate.update(sqlQuery, film.getName(), film.getReleaseDate(), film.getDescription(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        if (update == 0) {
            throw new NotFoundException("Фильм не найден!", HttpStatus.NOT_FOUND);
        }
        updateFilmGenresLinks(film);
        return findById(film.getId());
    }

    @Override
    public boolean addLike(Long filmId, Long userId) {
        String sqlQuery = "INSERT INTO FILMS_USERS (USER_ID,FILM_ID)" +
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
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM FILMS WHERE ID = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, id);
    }

    @Override
    public boolean delete(Long id) {
        String sqlQuery = "DELETE FROM FILMS " +
                "WHERE ID = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        String sqlQuery = "DELETE FROM FILMS_USERS " +
                "WHERE FILM_ID = ? AND USER_ID = ?";
        return jdbcTemplate.update(sqlQuery, filmId, userId) > 0;
    }

    @Override
    public List<Film> getPopularFilm(Integer count) {
        String sqlQuery = "SELECT F.*," +
                "       M.name               as mpa_name," +
                "       GROUP_CONCAT(G.id)   as genre_id," +
                "       GROUP_CONCAT(G.name) as genre_name," +
                "       COUNT(FU.USER_ID)" +
                "FROM FILMS AS F" +
                "         LEFT JOIN PUBLIC.MPA M on M.ID = F.MPA_ID" +
                "         LEFT JOIN PUBLIC.FILMS_GENRES FG on F.ID = FG.FILM_ID" +
                "         LEFT JOIN PUBLIC.GENRES G on G.ID = FG.GENRE_ID" +
                "         LEFT JOIN PUBLIC.FILMS_USERS FU on F.ID = FU.FILM_ID " +
                "GROUP BY F.ID " +
                "ORDER BY COUNT(USER_ID) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFilms, count);
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
                .build();
    }

    private void deleteFilmGenresLinks(Long filmId) {
        String sqlQuery = "DELETE FROM films_genres " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
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

    private MPA mapRowToMpa(ResultSet resultSet) throws SQLException {
        return MPA.builder()
                .id(resultSet.getLong("mpa_id"))
                .name(resultSet.getString("mpa_name"))
                .build();
    }
}
