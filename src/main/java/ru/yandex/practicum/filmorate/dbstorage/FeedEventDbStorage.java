package ru.yandex.practicum.filmorate.dbstorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dbstorage.dao.FeedEventStorageDao;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.sql.*;
import java.util.List;

@Component
public class FeedEventDbStorage implements FeedEventStorageDao {
    private final JdbcTemplate jdbcTemplate;

    public FeedEventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int createFeedEvent(FeedEvent feedEvent) {
        String sqlQuery = "insert into feed_event (event_time, event_type, event_operation, user_id, entity_id) " +
                "values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sqlQuery, new String[]{"event_id"});
            statement.setLong(1, feedEvent.getTimestamp());
            statement.setString(2, feedEvent.getEventType().name());
            statement.setString(3, feedEvent.getOperation().name());
            statement.setInt(4, feedEvent.getUserId());
            statement.setInt(5, feedEvent.getEntityId());
            return statement;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Override
    public List<FeedEvent> getFeedEvents(int idUser) {
        String sql = "select * from feed_event as f_e" +
                " where f_e.user_id = ?"/* +
                " order by event_time desc"*/;
        return jdbcTemplate.query(sql, FeedEventDbStorage::mapRowToModel, idUser);
    }

    static FeedEvent mapRowToModel(ResultSet resultSet, int rowNum) throws SQLException {
        return FeedEvent.builder()
                .timestamp(resultSet.getLong("event_time"))
                .eventType(EventType.valueOf(resultSet.getString("event_type")))
                .operation(EventOperation.valueOf(resultSet.getString("event_operation")))
                .userId(resultSet.getInt("user_id"))
                .eventId(resultSet.getInt("event_id"))
                .entityId(resultSet.getInt("entity_id"))
                .build();
    }
}
