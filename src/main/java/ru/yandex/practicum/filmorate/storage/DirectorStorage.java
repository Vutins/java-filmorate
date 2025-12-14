package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {

    Director create(Director director);

    int update(Director director);

    int delete(Long id);

    Optional<Director> getById(Long id);

    List<Director> getAll();

    void addDirectorToFilm(Film film);

    int deleteDirectorsFromFilm(Long filmId);

    Set<Director> getDirectorsByFilmId(Long filmId);

    Map<Long, Set<Director>> getAllDirectorsByFilm();
}
