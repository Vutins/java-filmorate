package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.dao.repositories.RatingDbStorage;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({RatingDbStorage.class, RatingRowMapper.class})
public class RatingControllerTest {

    private final RatingDbStorage ratingDbStorage;

    @Test
    @DirtiesContext
    public void testGetAllRatings() {
        List<Rating> ratings = ratingDbStorage.getAllRatings();
        assertFalse(ratings.isEmpty());
        assertEquals(5, ratings.size());
    }

    @Test
    @DirtiesContext
    public void testGetRatingById() {
        Rating rating = ratingDbStorage.getRatingById(1);
        assertThat(rating).hasFieldOrPropertyWithValue("id", 1);
        assertThat(rating).hasFieldOrPropertyWithValue("name", "G");

        rating = ratingDbStorage.getRatingById(2);
        assertThat(rating).hasFieldOrPropertyWithValue("id", 2);
        assertThat(rating).hasFieldOrPropertyWithValue("name", "PG");

        rating = ratingDbStorage.getRatingById(3);
        assertThat(rating).hasFieldOrPropertyWithValue("id", 3);
        assertThat(rating).hasFieldOrPropertyWithValue("name", "PG-13");
    }

    @Test
    @DirtiesContext
    public void testGetAllRatingsOrder() {
        List<Rating> ratings = ratingDbStorage.getAllRatings();
        assertEquals(1, ratings.get(0).getId());
        assertEquals(2, ratings.get(1).getId());
        assertEquals(3, ratings.get(2).getId());
        assertEquals(4, ratings.get(3).getId());
        assertEquals(5, ratings.get(4).getId());
    }

    @Test
    @DirtiesContext
    public void testGetNonExistentRating() {
        assertThrows(RuntimeException.class, () -> ratingDbStorage.getRatingById(999));
    }

    @Test
    @DirtiesContext
    public void testRatingNames() {
        List<Rating> ratings = ratingDbStorage.getAllRatings();
        assertEquals("G", ratings.get(0).getName());
        assertEquals("PG", ratings.get(1).getName());
        assertEquals("PG-13", ratings.get(2).getName());
        assertEquals("R", ratings.get(3).getName());
        assertEquals("NC-17", ratings.get(4).getName());
    }
}