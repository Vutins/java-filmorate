package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {

    private final GenreStorage genreStorage;

    public List<Genre> getAllGenres() {
        return List.copyOf(genreStorage.getAllGenres());
    }

    public Genre getGenreById(Integer id) {
        if (id == null) {
            throw new ValidationException("Жанр не может быть получен по ID = null");
        }
        return genreStorage.getGenreById(id);
    }
}