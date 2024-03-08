package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.EventDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@AllArgsConstructor
public class EventDaoImpl implements EventDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFriendsFeed(Long userId) {
        String sql= "SELECT * FROM events e " +
                "WHERE e.user_id IN " +
                "(SELECT u.id " +
                "FROM users u " +
                "WHERE u.id IN " +
                "(SELECT f.user2_id " +
                "FROM friendships AS f " +
                "WHERE f.user1_id=:userId " +
                "UNION " +
                "SELECT f.user1_id " +
                "FROM friendships AS f   " +
                "WHERE f.user2_id=:userId " +
                "AND f.status = TRUE))";
        SqlParameterSource parameterSource = new MapSqlParameterSource("user_id", userId);
        return jdbcTemplate.query(sql, parameterSource, (rs, rowNum) -> mapRow(rs, rowNum));
    }

    @Override
    public Event save(Event event) {
        String sql = "INSERT INTO events (user_id, event_type, entity_id, operation) " +
                "VALUES (:user_id, :event_type, :entity_id, :operation)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("user_id", event.getUserId())
                .addValue("event_type_id", event.getEventType().name())
                .addValue("entity_id", event.getEntityId())
                .addValue("operation_id", event.getOperation().name());

        jdbcTemplate.update(sql, parameterSource, keyHolder);

        final Long id = keyHolder.getKeyAs(Long.class);

        event.setId(id);

        return event;
    }

    private Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .id(rs.getLong("id"))
                .userId(rs.getLong("user_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .entityId(rs.getLong("entity_id"))
                .timestamp(rs.getTimestamp("timestamp").toLocalDateTime())
                .build();
    }
}
