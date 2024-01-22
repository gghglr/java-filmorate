package ru.yandex.practicum.filmorate.dbstorage.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorageDao {

    List<Director> findAll();

    Director findById(int id);

    Director create(Director director);

    Director update(Director director);

    void delete(int id);
}
