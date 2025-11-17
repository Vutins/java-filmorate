package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    void delete(Integer id);

    Film getFilm(Integer id);

    List<Film> getAllFilms();

    List<Integer> getAllKeys();
}