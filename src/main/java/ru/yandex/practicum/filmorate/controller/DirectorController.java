package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Director> getAll() {
        log.info("Получен Http-запрос на возврат всех режиссёров");
        List<Director> directors = directorService.getAll();
        log.info("Успешно обработан Http-запрос на возврат всех режиссёров");
        return directors;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    Director getById(@PathVariable("id") Long id) {
        log.info("Получен Http-запрос на возврат режиссёра по ID");
        Director director = directorService.getById(id);
        log.info("Успешно обработан Http-запрос на возврат режиссёра по ID");
        return director;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) //под вопросом
    Director create(@Valid @RequestBody Director director) {
        log.info("Получен Http-запрос на создание режиссёра {}", director);
        Director result = directorService.create(director);
        log.info("Успешно обработан Http-запрос на создание режиссёра {}", director);
        return result;
    }

    @PutMapping
    Director update(@Valid @RequestBody Director director) {
        log.info("Получен Http-запрос на обновление режиссёра {}", director);
        Director result = directorService.update(director);
        log.info("Успешно обработан Http-запрос на обновление режиссёра {}", director);
        return result;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("id") Long id) {
        log.info("Получен Http-запрос на удаление режиссёра по ID");
        directorService.delete(id);
        log.info("Успешно обработан Http-запрос на удаление режиссёра по ID");
    }
}
