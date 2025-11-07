package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User create(User user) {
        UserValidator.validate(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        if (!userStorage.getAllKeys().contains(user.getId())) {
            throw new NotFoundException("обновление несуществующего пользователя");
        }
        UserValidator.validate(user);
        return userStorage.update(user);
    }

    public void delete(Integer id) {
        if (!userStorage.getAllUsers().contains(userStorage.getUser(id))) {
            throw new NotFoundException("удаление несуществующего пользователя");
        }
        userStorage.delete(id);
    }

    public User getUser(Integer id) {
        if (userStorage.getUser(id) == null) {
            throw new NotFoundException("вызов несуществующего пользователя");
        }
        return userStorage.getUser(id);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void putFriend(User user, User userFriend) {
        user.getFriends().add(userFriend);
        userFriend.getFriends().add(user);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        userStorage.getUser(id).getFriends().remove(userStorage.getUser(friendId));
        userStorage.getUser(friendId).getFriends().remove(userStorage.getUser(id));
    }

    public List<User> getListMutualFriends(Integer user1, Integer user2) {
    List<User> mutualFriends = new ArrayList<>();
        for (User userFirst : userStorage.getUser(user1).getFriends()) {
            for (User userSecond : userStorage.getUser(user2).getFriends()) {
                if (userFirst.equals(userSecond)) {
                    mutualFriends.add(userFirst);
                }
            }
        }
        return mutualFriends;
    }
}
