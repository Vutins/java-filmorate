package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getAllFilms() {
        log.info("Запрос на получение списка всех фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@PathVariable Long id) {
        log.info("Запрос на получение фильма по ID");
        return filmService.getFilmById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("Запрос на получение списка n-лучших фильмов по кол-ву лайков");
        return filmService.getPopularFilms(count);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Запрос на добавление фильма в приложение");
        return filmService.create(film);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Запрос на обновление данных фильма");
        return filmService.update(film);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос на добавление лайка к фильму по ID пользователя");
        filmService.addLike(id, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос на удаление лайка у фильма по ID пользователя");
        filmService.removeLike(id, userId);
    }
}