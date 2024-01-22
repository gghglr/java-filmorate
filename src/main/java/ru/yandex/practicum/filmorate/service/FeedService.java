package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dbstorage.FeedEventDbStorage;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class FeedService {
    private final FeedEventDbStorage feedEventDbStorage;
    private Logger log = LoggerFactory.getLogger(FilmService.class);

    @Autowired
    public FeedService(FeedEventDbStorage feedEventDbStorage) {
        this.feedEventDbStorage = feedEventDbStorage;
    }

    public List<FeedEvent> getFeedForUser(int idUser) {
        return feedEventDbStorage.getFeedEvents(idUser);
    }

    void addReviewEvent(int idUser, int idReview) {
        FeedEvent feedEvent = new FeedEvent(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), EventType.REVIEW, EventOperation.ADD, idUser, 0, idReview);
        feedEventDbStorage.createFeedEvent(feedEvent);
    }

    void createDeleteReviewEvent(int idUser, int idReview) {
        FeedEvent feedEvent = new FeedEvent(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), EventType.REVIEW, EventOperation.REMOVE, idUser, 0, idReview);
        feedEventDbStorage.createFeedEvent(feedEvent);
    }

    void updateReviewEvent(int idUser, int idReview) {
        FeedEvent feedEvent = new FeedEvent(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), EventType.REVIEW, EventOperation.UPDATE, idUser, 0, idReview);
        feedEventDbStorage.createFeedEvent(feedEvent);
    }

    void addFriendsEvent(int idUser, int idFriend) {
        FeedEvent feedEvent = new FeedEvent(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), EventType.FRIEND, EventOperation.ADD, idUser, 0, idFriend);
        feedEventDbStorage.createFeedEvent(feedEvent);
    }

    void createDeleteFriendsEvent(int idUser, int idFriend) {
        FeedEvent feedEvent = new FeedEvent(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), EventType.FRIEND, EventOperation.REMOVE, idUser, 0, idFriend);
        feedEventDbStorage.createFeedEvent(feedEvent);
    }

    void addLikesEvent(int idUser, int idFilm) {
        FeedEvent feedEvent = new FeedEvent(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), EventType.LIKE, EventOperation.ADD, idUser, 0, idFilm);
        feedEventDbStorage.createFeedEvent(feedEvent);
    }

    void createDeleteLikesEvent(int idUser, int idFilm) {
        FeedEvent feedEvent = new FeedEvent(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), EventType.LIKE, EventOperation.REMOVE, idUser, 0, idFilm);
        feedEventDbStorage.createFeedEvent(feedEvent);
    }
}