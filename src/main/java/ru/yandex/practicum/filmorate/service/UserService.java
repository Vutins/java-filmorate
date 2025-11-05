package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public void create(User user) {
        userStorage.create(user);
    }

    public void update(User user) {
        userStorage.update(user);
    }

    public void delete(User user) {
        userStorage.delete(user);
    }

    public User getUser(Integer id) {
        return userStorage.getUser(id);
    }

    public void addFriends(User user, User userFriend) {
        user.getFriends().add(userFriend);
        userFriend.getFriends().add(user);
    }

    public void deleteFriend(User user, User userFriend) {
        user.getFriends().remove(userFriend);
        userFriend.getFriends().remove(user);
    }

    public List<User> getListMutualFriends(User user1, User user2) {
    List<User> mutualFriends = new ArrayList<>();
        for (User userFirst : user1.getFriends()) {
            for (User userSecond : user2.getFriends()) {
                if (userFirst.equals(userSecond)) {
                    mutualFriends.add(userFirst);
                }
            }
        }
        return mutualFriends;
    }
}
