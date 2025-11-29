package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dao.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dao.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.dao.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.dao.repositories.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.repositories.GenreDbStorage;
import ru.yandex.practicum.filmorate.dao.repositories.RatingDbStorage;
import ru.yandex.practicum.filmorate.dao.repositories.UserDbStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class, GenreDbStorage.class, GenreRowMapper.class,
        RatingDbStorage.class, RatingRowMapper.class, FilmDbStorage.class, FilmRowMapper.class})
public class FilmControllerTest {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    @BeforeEach
    void setUp() {
        User user = new User(
                0L,
                "Test User",
                "user@mail.com",
                "login",
                LocalDate.of(1990, 1, 1)
        );
        userDbStorage.create(user);

        Rating mpa = new Rating(1, "G");
        Film film = new Film(
                0L,
                "Test Film",
                "Test Description",
                LocalDate.of(2000, 1, 1),
                120,
                Collections.unmodifiableSequencedSet(new LinkedHashSet<>()),
                mpa
        );
        filmDbStorage.create(film);
    }

    @Test
    @DirtiesContext
    public void testFindFilmById() {
        Film film = filmDbStorage.getFilmById(1L);
        assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(film).hasFieldOrPropertyWithValue("name", "Test Film");
        assertThat(film).hasFieldOrPropertyWithValue("duration", 120);
    }

    @Test
    @DirtiesContext
    public void testUpdateFilm() {
        Film updatedFilm = new Film(
                1L,
                "Updated Film",
                "Updated Description",
                LocalDate.of(2001, 1, 1),
                130,
                Collections.unmodifiableSequencedSet(new LinkedHashSet<>()),
                new Rating(2, "PG")
        );
        filmDbStorage.update(updatedFilm);
        Film film = filmDbStorage.getFilmById(1L);
        assertThat(film).hasFieldOrPropertyWithValue("name", "Updated Film");
        assertThat(film).hasFieldOrPropertyWithValue("duration", 130);
    }

    @Test
    @DirtiesContext
    public void testGetAllFilms() {
        List<Film> films = filmDbStorage.getAllFilms();
        assertFalse(films.isEmpty());
        assertEquals(1, films.size());
    }

    @Test
    @DirtiesContext
    public void testGetTopFilms() {
        Film film2 = new Film(
                0L,
                "Film 2",
                "Description 2",
                LocalDate.of(2002, 1, 1),
                110,
                Collections.unmodifiableSequencedSet(new LinkedHashSet<>()),
                new Rating(2, "PG")
        );
        filmDbStorage.create(film2);

        User user2 = new User(
                0L,
                "User 2",
                "user2@mail.com",
                "login2",
                LocalDate.of(1991, 1, 1)
        );
        userDbStorage.create(user2);

        filmDbStorage.addLike(1L, 1L);
        filmDbStorage.addLike(2L, 1L);
        filmDbStorage.addLike(2L, 2L);

        List<Film> topFilms = filmDbStorage.getTopFilms(10);
        assertEquals(2, topFilms.size());
        assertEquals(2, topFilms.get(0).getId());
        assertEquals(1, topFilms.get(1).getId());
    }

    @Test
    @DirtiesContext
    public void testFilmWithGenres() {
        Set<Genre> genres = Set.of(new Genre(1, "Комедия"), new Genre(2, "Драма"));
        Film filmWithGenres = new Film(
                0L,
                "Film with Genres",
                "Description",
                LocalDate.of(2003, 1, 1),
                100,
                genres,
                new Rating(1, "G")
        );
        Film created = filmDbStorage.create(filmWithGenres);
        Film retrieved = filmDbStorage.getFilmById(created.getId());
        assertEquals(2, retrieved.getGenres().size());
    }

    @Test
    @DirtiesContext
    public void testAddAndRemoveLike() {
        filmDbStorage.addLike(1L, 1L);
        Film film = filmDbStorage.getFilmById(1L);

        filmDbStorage.removeLike(1L, 1L);
        List<Film> topFilms = filmDbStorage.getTopFilms(10);
        assertEquals(1, topFilms.size());
    }

    @Test
    @DirtiesContext
    public void testCreateMultipleFilms() {
        Film film2 = new Film(
                0L,
                "Film 2",
                "Description 2",
                LocalDate.of(2002, 1, 1),
                110,
                Collections.unmodifiableSequencedSet(new LinkedHashSet<>()),
                new Rating(2, "PG")
        );
        Film created = filmDbStorage.create(film2);
        assertNotNull(created.getId());
        assertEquals("Film 2", created.getName());
    }
}