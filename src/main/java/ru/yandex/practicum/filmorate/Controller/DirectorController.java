package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public List<Director> findAll() {
        log.info("Получен запрос на получение всех режиссеров");
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable("id") int id) {
        log.info("Получен запрос на поиск режиссера фильма с id {}", id);
        return directorService.findById(id);
    }

    @PostMapping
    public Director create(@RequestBody Director director) {
        log.info("Получен запрос на добавление режиссера в базу данных");
        validate(director);
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@RequestBody Director director) {
        validate(director);
        log.info("Получен запрос на изменение режиссера с id {}", director.getId());
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        log.info("Получен запрос на удаление режиссера с id {}", id);
        directorService.delete(id);
    }

    void validate(Director director) {
        if (director.getName().isBlank() || director.getName() == null || director.getName().equals(" ")) {
            log.info("Пользователь неверно ввел имя режиссера: {}", director.getName());
            throw new ValidationException("Название режиссера не может быть пустым");
        }
    }
}
