package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer,Film> filmMap;

    public void create(Film film) {
        FilmValidator.validate(film);
        filmMap.put(film.getId(), film);
    }

    public void update(Film film) {
        FilmValidator.validate(film);
        filmMap.put(film.getId(), film);
    }

    public void delete(Film film) {
        filmMap.remove(film.getId());
    }

    public Film getFilm(Integer id) {
        if (!filmMap.containsValue(id)) {
            return null;
        } else {
            return filmMap.get(id);
        }
    }
}
