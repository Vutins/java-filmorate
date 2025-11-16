package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film create(Film film) {
        FilmValidator.validate(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (!filmStorage.getAllKeys().contains(film.getId())) {
            throw new NotFoundException("обновление несуществующего фильма");
        }
        FilmValidator.validate(film);
        return filmStorage.update(film);
    }

    public void delete(Integer id) {
        if (!filmStorage.getAllFilms().contains(filmStorage.getFilm(id))) {
            throw new NotFoundException("удаление несуществующего фильма");
        }
        filmStorage.delete(id);
    }

    public Film getFilm(Integer id) {
        if (filmStorage.getFilm(id) == null) {
            throw new NotFoundException("вызов несуществующего фильма");
        }
        return filmStorage.getFilm(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void putLike(Integer id, Integer userId) {
        if (filmStorage.getFilm(id) == null) {
            throw new NotFoundException("Фильм не найден");
        }
        if (userStorage.getUser(userId) == null) {
            throw new NotFoundException("пользователь не найден");
        }
        if (filmStorage.getFilm(id).getLikes() == null) {
            filmStorage.getFilm(id).setLikes(new HashSet<>());
        }
        filmStorage.getFilm(id).getLikes().add(userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);

        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }

        if (film.getLikes() == null || !film.getLikes().contains(userId)) {
            throw new NotFoundException("Лайк не найден");
        }
        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        int filmsCount = (count == null) ? 10 : count;

        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(filmsCount)
                .collect(Collectors.toList());
    }
}