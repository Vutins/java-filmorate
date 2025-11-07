package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer,Film> filmMap = new HashMap<>();
    private Integer id = 1;

    public Film create(Film film) {
        film.setId(id);
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
        if (!filmMap.containsKey(id)) {
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
