package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> getAll() {
        log.info("Вывод всех рейтингов");
        return mpaStorage.findAll();
    }

    public Mpa findById(Integer id) {
        log.info("Рейтинг с id " + id);
        return mpaStorage.mpaById(id);
    }
}