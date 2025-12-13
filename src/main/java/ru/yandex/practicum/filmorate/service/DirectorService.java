package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public List<Director> getAll() {
        log.debug("Вызван метод getAll() в DirectorService");
        List<Director> directors = directorStorage.getAll();
        log.info("Успешно обработан метод getAll() в DirectorService");
        return directors;
    }

    public Director getById(Long id) {
        log.debug("Вызван метод getById() в DirectorService");
        Optional<Director> director = directorStorage.getById(id);
        if (!director.isPresent()) {
            throw new NotFoundException("Режиссёр с таким ID: " + id + " не найден");
        }
        Director directorResult = director.get();
        log.info("Успешно обработан метод getById() в DirectorService");
        return directorResult;
    }

    public Director create(Director director) {
        log.debug("Вызван метод create() в DirectorService");
        Director directorResult = directorStorage.create(director);
        log.info("Успешно обработан метод create() в DirectorService");
        return directorResult;
    }

    public Director update(Director director) {
        log.debug("Вызван метод update() в DirectorService");
        Director directorResult = directorStorage.update(director);
        log.info("Успешно обработан метод update() в DirectorService");
        return directorResult;
    }

    public void delete(Long id) {
        log.debug("Вызван метод delete() в DirectorService");
        int result = directorStorage.delete(id);
        if (result == 0) {
            throw new NotFoundException("Режиссёр с таким ID: " + id + " не найден");
        }
        log.info("Успешно обработан метод delete() в DirectorService");
    }
}
