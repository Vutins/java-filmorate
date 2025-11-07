package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer,Film> filmMap;
    private Integer id = 1;

    public Film create(Film film) {
        filmMap.put(id++, film);
        return film;
    }

    public Film update(Film film) {
        filmMap.put(film.getId(), film);
        return film;
    }

    public void delete(Integer id) {
        filmMap.remove(id);
    }

    public Film getFilm(Integer id) {
        if (!filmMap.containsValue(id)) {
            return null;
        } else {
            return filmMap.get(id);
        }
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(filmMap.values());
    }

    public List<Integer> getAllKeys() {
        return new ArrayList<>(filmMap.keySet());
    }


}
