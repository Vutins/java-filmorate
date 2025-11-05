package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;

    public void create(Film film) {
        filmStorage.create(film);
    }

    public void update(Film film) {
        filmStorage.update(film);
    }

    public void delete(Film film) {
        filmStorage.delete(film);
    }

    public Film getFilm(Integer id) {
         return filmStorage.getFilm(id);
    }

    public void addLike(User user, Film film) {
        film.getLikes().add(user);
    }

    public void deleteLike (User user, Film film) {
        film.getLikes().remove(user);
    }
}
