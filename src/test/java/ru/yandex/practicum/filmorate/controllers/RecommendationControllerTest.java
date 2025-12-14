package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.mappers.*;
import ru.yandex.practicum.filmorate.dao.repositories.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        UserDbStorage.class, UserRowMapper.class,
        FilmDbStorage.class, FilmRowMapper.class,
        GenreDbStorage.class, GenreRowMapper.class,
        RatingDbStorage.class, RatingRowMapper.class,
        UserService.class
})
public class RecommendationControllerTest {

    private final UserService userService;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    @BeforeEach
    void setUp() {
        // Создаем пользователей
        User user1 = new User(0L, "User 1", "user1@mail.com", "login1", LocalDate.of(1990, 1, 1));
        User user2 = new User(0L, "User 2", "user2@mail.com", "login2", LocalDate.of(1991, 1, 1));
        User user3 = new User(0L, "User 3", "user3@mail.com", "login3", LocalDate.of(1992, 1, 1));

        userDbStorage.create(user1);
        userDbStorage.create(user2);
        userDbStorage.create(user3);

        // Создаем фильмы
        Rating mpa = new Rating(1, "G");

        Film film1 = new Film(0L, "Film 1", "Description 1", LocalDate.of(2000, 1, 1), 120,
                Collections.unmodifiableSequencedSet(new LinkedHashSet<>()), mpa, Collections.emptySet());
        Film film2 = new Film(0L, "Film 2", "Description 2", LocalDate.of(2001, 1, 1), 130,
                Collections.unmodifiableSequencedSet(new LinkedHashSet<>()), mpa, Collections.emptySet());
        Film film3 = new Film(0L, "Film 3", "Description 3", LocalDate.of(2002, 1, 1), 140,
                Collections.unmodifiableSequencedSet(new LinkedHashSet<>()), mpa, Collections.emptySet());
        Film film4 = new Film(0L, "Film 4", "Description 4", LocalDate.of(2003, 1, 1), 150,
                Collections.unmodifiableSequencedSet(new LinkedHashSet<>()), mpa, Collections.emptySet());

        filmDbStorage.create(film1);
        filmDbStorage.create(film2);
        filmDbStorage.create(film3);
        filmDbStorage.create(film4);

        // Добавляем лайки
        filmDbStorage.addLike(1L, 1L); // User1 лайкает Film1
        filmDbStorage.addLike(2L, 1L); // User1 лайкает Film2
        filmDbStorage.addLike(1L, 2L); // User2 лайкает Film1
        filmDbStorage.addLike(2L, 2L); // User2 лайкает Film2
        filmDbStorage.addLike(3L, 2L); // User2 лайкает Film3
        filmDbStorage.addLike(4L, 3L); // User3 лайкает Film4
    }

    @Test
    @DirtiesContext
    public void testGetRecommendations() {
        // User1 и User2 имеют общие лайки (Film1, Film2)
        // User2 также лайкнул Film3, который User1 не лайкнул
        // Ожидаем, что User1 получит рекомендацию Film3

        List<Film> recommendations = userService.getRecommendations(1L);

        assertEquals(1, recommendations.size());
        assertEquals(3L, recommendations.get(0).getId());
        assertEquals("Film 3", recommendations.get(0).getName());
    }

    @Test
    @DirtiesContext
    public void testNoRecommendationsWhenNoSimilarUsers() {
        // User3 лайкнул только Film4, у других пользователей другие лайки
        // У User3 нет пользователей с похожими вкусами

        List<Film> recommendations = userService.getRecommendations(3L);

        assertTrue(recommendations.isEmpty());
    }

    @Test
    @DirtiesContext
    public void testNoRecommendationsWhenUserHasNoLikes() {
        // Создаем пользователя без лайков
        User user4 = new User(0L, "User 4", "user4@mail.com", "login4", LocalDate.of(1993, 1, 1));
        userDbStorage.create(user4);

        List<Film> recommendations = userService.getRecommendations(4L);

        assertTrue(recommendations.isEmpty());
    }
}