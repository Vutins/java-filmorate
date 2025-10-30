package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    @PutMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("создание film");
        FilmValidator.validate(film);
        return film;
    }

    @PostMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("обновление film");
        FilmValidator.validate(film);
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("вывод всех films");
        return new ArrayList<Film>();
    }
}
