package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {

    @PutMapping
    public Film create(@Valid @RequestBody Film film) {
        return film;
    }

    @PostMapping
    public Film update(@Valid @RequestBody Film film) {
        return film;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<Film>();
    }
}
