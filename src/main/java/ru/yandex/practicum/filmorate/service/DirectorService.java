package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dbstorage.dao.DirectorStorageDao;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
public class DirectorService {
    private final DirectorStorageDao directorStorageDao;

    @Autowired
    public DirectorService(DirectorStorageDao directorStorageDao) {
        this.directorStorageDao = directorStorageDao;
    }

    public List<Director> findAll() {
        return directorStorageDao.findAll();
    }

    public Director findById(int id) {
        return directorStorageDao.findById(id);
    }

    public Director create(Director director) {
        return directorStorageDao.create(director);
    }

    public Director update(Director director) {
        return directorStorageDao.update(director);
    }

    public void delete(int id) {
        directorStorageDao.delete(id);
    }
}
