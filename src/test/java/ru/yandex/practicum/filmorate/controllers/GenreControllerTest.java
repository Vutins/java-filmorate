package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dao.repositories.GenreDbStorage;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class, GenreRowMapper.class})
public class GenreControllerTest {

    private final GenreDbStorage genreDbStorage;

    @Test
    @DirtiesContext
    public void testGetAllGenres() {
        List<Genre> genres = genreDbStorage.getAllGenres();
        assertFalse(genres.isEmpty());
        assertEquals(6, genres.size());
    }

    @Test
    @DirtiesContext
    public void testGetGenreById() {
        Genre genre = genreDbStorage.getGenreById(1);
        assertThat(genre).hasFieldOrPropertyWithValue("id", 1);
        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");

        genre = genreDbStorage.getGenreById(2);
        assertThat(genre).hasFieldOrPropertyWithValue("id", 2);
        assertThat(genre).hasFieldOrPropertyWithValue("name", "Драма");

        genre = genreDbStorage.getGenreById(3);
        assertThat(genre).hasFieldOrPropertyWithValue("id", 3);
        assertThat(genre).hasFieldOrPropertyWithValue("name", "Мультфильм");
    }

    @Test
    @DirtiesContext
    public void testGetAllGenresOrder() {
        List<Genre> genres = genreDbStorage.getAllGenres();
        assertEquals(1, genres.get(0).getId());
        assertEquals(2, genres.get(1).getId());
        assertEquals(3, genres.get(2).getId());
        assertEquals(4, genres.get(3).getId());
        assertEquals(5, genres.get(4).getId());
        assertEquals(6, genres.get(5).getId());
    }

    @Test
    @DirtiesContext
    public void testGetNonExistentGenre() {
        assertThrows(RuntimeException.class, () -> genreDbStorage.getGenreById(999));
    }
}