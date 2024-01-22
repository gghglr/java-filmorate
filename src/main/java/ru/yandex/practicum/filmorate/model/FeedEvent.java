package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FeedEvent {
    long timestamp;
    EventType eventType;
    EventOperation operation;
    int userId;
    int eventId;
    int entityId;
}