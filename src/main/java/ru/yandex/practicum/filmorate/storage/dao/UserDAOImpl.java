package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDAO;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository(value = "userDB")
@RequiredArgsConstructor
public class UserDAOImpl implements UserDAO {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User save(User user) {
        String sqlQuery = "INSERT INTO USERS(NAME, EMAIL, LOGIN, BIRTHDAY) VALUES ( ?, ?, ?, ? )";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(sqlQuery, new String[]{"id"});
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getLogin());
            statement.setDate(4, Date.valueOf(user.getBirthday()));
            return statement;
        }, keyHolder);
        Long userId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setId(userId);
        return findById(userId).orElse(null);
    }

    @Override
    public Optional<User> findById(Long id) {
        String sqlQuery = "SELECT *" +
                "FROM USERS " +
                "WHERE ID = ?";
        List<User> users = jdbcTemplate.query(sqlQuery, this::mapRowToUsers, id);
        return users.stream().findFirst();
    }


    @Override
    public List<User> findAll() {
        String sqlQuery = "SELECT * FROM USERS ";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUsers);
    }

    @Override
    public Optional<User> update(User user) {
        String sqlQuery = "UPDATE USERS SET " +
                "name = ?, email = ?, login = ?, " +
                "birthday = ? " +
                "WHERE id = ?";
        Long userId = user.getId();
        int rowsUpdated = jdbcTemplate.update(sqlQuery, user.getName(), user.getEmail(), user.getLogin(),
                user.getBirthday(), userId);
        if (rowsUpdated == 0) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    public boolean deleteFriend(Long id, Long friendId) {
        String sqlQuery = "DELETE FROM FRIENDSHIPS " +
                "WHERE (USER1_ID = ? AND USER2_ID = ?)" +
                "OR (USER1_ID = ? AND USER2_ID = ?)";
        return jdbcTemplate.update(sqlQuery, id, friendId, friendId, id) > 0;
    }

    @Override
    public boolean isExistById(Long id) {
        String sqlQuery = "SELECT EXISTS(SELECT 1 FROM USERS WHERE ID = ?)";
        return jdbcTemplate.queryForObject(sqlQuery, Boolean.class, id);
    }

    @Override
    public boolean addFriend(Long id, Long friendId) {
        String sqlQuery = "INSERT INTO FRIENDSHIPS(USER1_ID, USER2_ID) " +
                "SELECT ?, ? " +
                "FROM DUAl " +
                "WHERE NOT EXISTS " +
                "(SELECT 1 FROM " +
                "FRIENDSHIPS " +
                "WHERE (USER1_ID = ? AND USER2_ID = ?) " +
                "OR (USER1_ID = ? AND USER2_ID = ?))";
        return jdbcTemplate.update(sqlQuery, id, friendId, id, friendId, friendId, id) > 0;
    }

    @Override
    public List<User> getAllFriends(Long id) {
        String sqlQuery = "SELECT u.* " +
                "FROM USERS u " +
                "WHERE u.id IN " +
                "(SELECT f.USER2_ID " +
                "FROM FRIENDSHIPS f " +
                "WHERE f.USER1_ID = ? " +
                "UNION " +
                "SELECT f.USER1_ID " +
                "FROM FRIENDSHIPS f   " +
                "WHERE f.USER2_ID = ? " +
                "AND f.status = TRUE)";
        List<User> users = jdbcTemplate.query(sqlQuery, this::mapRowToUsers, id, id);
        return users.stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getAllMutualFriends(Long id, Long otherId) {
        String sqlQuery = "SELECT *" +
                "FROM USERS " +
                "WHERE ID IN ( SELECT * FROM (" +
                "    SELECT f.USER2_ID" +
                "    FROM FRIENDSHIPS f" +
                "    WHERE f.USER1_ID = ?" +
                "    UNION" +
                "    SELECT f.USER1_ID" +
                "    FROM FRIENDSHIPS f" +
                "    WHERE f.USER2_ID = ? AND f.STATUS = TRUE)" +
                "    INTERSECT" +
                "    (SELECT f.USER2_ID" +
                "    FROM FRIENDSHIPS f" +
                "    WHERE f.USER1_ID = ?" +
                "    UNION" +
                "    SELECT f.USER1_ID" +
                "    FROM FRIENDSHIPS f" +
                "    WHERE f.USER2_ID = ? AND f.STATUS = TRUE))";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUsers, id, id, otherId, otherId);
    }

    private User mapRowToUsers(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
