package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository(value = "genreDB")
@RequiredArgsConstructor
public class GenreDAOImpl implements GenreDAO {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Genre> findById(Long id) {
        String sqlQuery = "SELECT *" +
                "FROM genres " +
                "WHERE id=?";
        List<Genre> genreList = jdbcTemplate.query(sqlQuery, this::mapRowToGenre, id);
        return genreList.stream().findFirst();
    }

    @Override
    public List<Genre> findAll() {
        String sqlQuery = "SELECT *" +
                "FROM genres";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre);
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
