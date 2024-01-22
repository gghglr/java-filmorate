package ru.yandex.practicum.filmorate.dbstorage.dao;

import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.util.List;

public interface FeedEventStorageDao {
    int createFeedEvent(FeedEvent feedEvent);

    List<FeedEvent> getFeedEvents(int idUser);
}