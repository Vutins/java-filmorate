package ru.yandex.practicum.filmorate.dao.repositories;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventDbStorageTest {
    private final EventDbStorage eventDbStorage;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final ReviewDbStorage reviewDbStorage;
    private Long user1Id;
    private Long user2Id;
    private Long filmId;
    private Long reviewId;

    @BeforeEach
    void setUp() {
        User user1 = new User(
                0L,
                "User 1",
                "user1@mail.com",
                "login1",
                LocalDate.of(1991, 1, 1)
        );
        user1Id = userDbStorage.create(user1).getId();

        User user2 = new User(
                0L,
                "User 2",
                "user2@mail.com",
                "login2",
                LocalDate.of(1991, 1, 1)
        );
        user2Id = userDbStorage.create(user2).getId();

        Film film = new Film(
                0L,
                "Test Film",
                "Test Description",
                LocalDate.of(2000, 1, 1),
                120,
                Collections.unmodifiableSequencedSet(new LinkedHashSet<>()),
                new Rating(1, "G"),
                Collections.unmodifiableSequencedSet(new LinkedHashSet<>())
        );
        filmId = filmDbStorage.create(film).getId();

        Review review = Review.builder()
                .content("test review")
                .isPositive(true)
                .userId(user1Id)
                .filmId(filmId)
                .build();
        reviewId = reviewDbStorage.create(review).getId();
    }

    @Test
    @DirtiesContext
    public void testAddEventAndGetFeed() {
        // given
        int expectedSize = 3;
        // when
        Event eventFriend = eventDbStorage.addEvent(user1Id, EventTypes.FRIEND, OperationTypes.ADD, user2Id);
        Event eventLike = eventDbStorage.addEvent(user1Id, EventTypes.LIKE, OperationTypes.ADD, filmId);
        Event eventReview = eventDbStorage.addEvent(user1Id, EventTypes.REVIEW, OperationTypes.ADD, reviewId);
        List<Event> events = eventDbStorage.getFeed(user1Id);
        // then
        assertNotNull(eventFriend);
        assertEquals(EventTypes.FRIEND.name(), eventFriend.getEventType());
        assertEquals(OperationTypes.ADD.name(), eventFriend.getOperation());
        assertEquals(user2Id, eventFriend.getEntityId());

        assertNotNull(eventLike);
        assertEquals(EventTypes.LIKE.name(), eventLike.getEventType());
        assertEquals(OperationTypes.ADD.name(), eventLike.getOperation());
        assertEquals(filmId, eventLike.getEntityId());

        assertNotNull(eventReview);
        assertEquals(EventTypes.REVIEW.name(), eventReview.getEventType());
        assertEquals(OperationTypes.ADD.name(), eventReview.getOperation());
        assertEquals(reviewId, eventReview.getEntityId());

        assertNotNull(events);
        assertEquals(expectedSize, events.size());
    }
}