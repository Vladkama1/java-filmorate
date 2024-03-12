package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorDAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DirectorDAOImpl implements DirectorDAO {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Director> findById(Long id) {
        String sqlQuery = "SELECT *" +
                "FROM directors " +
                "WHERE id = ?";
        List<Director> directors = jdbcTemplate.query(sqlQuery, this::mapRowToDirector, id);
        return directors.stream().findFirst();
    }

    @Override
    public List<Director> findAll() {
        String sqlQuery = "SELECT *" +
                "FROM directors";
        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public Director save(Director director) {
        String sqlQuery = "INSERT INTO directors (name) " +
                "VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        Long directorId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        director.setId(directorId);
        return findById(directorId).orElse(null);
    }

    @Override
    public Optional<Director> update(Director director) {
        String sqlQuery = "UPDATE directors " +
                "SET name = ? " +
                "WHERE id = ?";
        int update = jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
        if (update == 0) {
            return Optional.empty();
        }
        return findById(director.getId());
    }

    @Override
    public boolean isExistById(Long id) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM directors WHERE id = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, id);
    }

    @Override
    public boolean delete(Long id) {
        String sqlQuery = "DELETE " +
                "FROM directors " +
                "WHERE id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
