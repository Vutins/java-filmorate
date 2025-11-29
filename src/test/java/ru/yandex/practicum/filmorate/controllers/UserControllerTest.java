package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.dao.repositories.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
public class UserControllerTest {

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
    }

    @Test
    @DirtiesContext
    public void testFindUserById() {
        User user = userDbStorage.getUserById(1L);
        assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(user).hasFieldOrPropertyWithValue("name", "Test User");
        assertThat(user).hasFieldOrPropertyWithValue("email", "user@mail.com");
    }

    @Test
    @DirtiesContext
    public void testUpdateUser() {
        User updatedUser = new User(
                1L,
                "Updated User",
                "updated@mail.com",
                "newlogin",
                LocalDate.of(1991, 1, 1)
        );
        userDbStorage.update(updatedUser);
        User user = userDbStorage.getUserById(1L);
        assertThat(user).hasFieldOrPropertyWithValue("name", "Updated User");
        assertThat(user).hasFieldOrPropertyWithValue("email", "updated@mail.com");
    }

    @Test
    @DirtiesContext
    public void testGetAllUsers() {
        List<User> users = userDbStorage.getAllUsers();
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }

    @Test
    @DirtiesContext
    public void testAddAndRemoveFriend() {
        User user2 = new User(
                0L,
                "User 2",
                "user2@mail.com",
                "login2",
                LocalDate.of(1991, 1, 1)
        );
        userDbStorage.create(user2);

        userDbStorage.addFriend(1L, 2L);
        List<User> friends = userDbStorage.getAllFriendsById(1L);
        assertEquals(1, friends.size());
        assertEquals(2L, friends.get(0).getId());

        userDbStorage.removeFriend(1L, 2L);
        friends = userDbStorage.getAllFriendsById(1L);
        assertTrue(friends.isEmpty());
    }

    @Test
    @DirtiesContext
    public void testGetCommonFriends() {
        User user2 = new User(
                0L,
                "User 2",
                "user2@mail.com",
                "login2",
                LocalDate.of(1991, 1, 1)
        );
        userDbStorage.create(user2);

        User user3 = new User(
                0L,
                "User 3",
                "user3@mail.com",
                "login3",
                LocalDate.of(1992, 1, 1)
        );
        userDbStorage.create(user3);

        userDbStorage.addFriend(1L, 3L);
        userDbStorage.addFriend(2L, 3L);

        Set<Long> friends1 = new HashSet<>(userDbStorage.getUserFriendsIdsById(1L));
        Set<Long> friends2 = new HashSet<>(userDbStorage.getUserFriendsIdsById(2L));

        friends1.retainAll(friends2);

        List<User> commonFriends = userDbStorage.getUsersByIdSet(friends1);

        assertEquals(1, commonFriends.size());
        assertEquals(3L, commonFriends.get(0).getId());
    }

    @Test
    @DirtiesContext
    public void testCreateMultipleUsers() {
        User user2 = new User(
                0L,
                "User 2",
                "user2@mail.com",
                "login2",
                LocalDate.of(1991, 1, 1)
        );
        User created = userDbStorage.create(user2);
        assertNotNull(created.getId());
        assertEquals("User 2", created.getName());

        List<User> users = userDbStorage.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    @DirtiesContext
    public void testUserWithoutNameUsesLogin() {
        User user = new User(
                0L,
                null,
                "noname@mail.com",
                "justlogin",
                LocalDate.of(1990, 1, 1)
        );
        User created = userDbStorage.create(user);
        assertNull(created.getName());
        assertEquals("justlogin", created.getLogin());
    }

    @Test
    @DirtiesContext
    public void testGetUserFriendsIds() {
        User user2 = new User(
                0L,
                "User 2",
                "user2@mail.com",
                "login2",
                LocalDate.of(1991, 1, 1)
        );
        userDbStorage.create(user2);

        userDbStorage.addFriend(1L, 2L);
        Set<Long> friendsIds = userDbStorage.getUserFriendsIdsById(1L);
        assertEquals(1, friendsIds.size());
        assertTrue(friendsIds.contains(2L));
    }

    @Test
    @DirtiesContext
    public void testGetUsersByIdSet() {
        User user2 = new User(
                0L,
                "User 2",
                "user2@mail.com",
                "login2",
                LocalDate.of(1991, 1, 1)
        );
        userDbStorage.create(user2);

        List<User> users = userDbStorage.getUsersByIdSet(Set.of(1L, 2L));
        assertEquals(2, users.size());
    }
}