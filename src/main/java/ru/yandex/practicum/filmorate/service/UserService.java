package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.HashSet;
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

    public void putFriend(Integer id, Integer friendId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        if (friend == null) {
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден");
        }

        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        if (friend.getFriends() == null) {
            friend.setFriends(new HashSet<>());
        }
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void deleteFriend(Integer id, Integer friendId) {
        if (userStorage.getUser(id) == null || userStorage.getUser(friendId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        userStorage.getUser(id).getFriends().remove(friendId);
        userStorage.getUser(friendId).getFriends().remove(id);
    }

    public List<User> getListMutualFriends(Integer user1, Integer user2) {
    List<User> mutualFriends = new ArrayList<>();
        for (Integer userFirst : userStorage.getUser(user1).getFriends()) {
            for (Integer userSecond : userStorage.getUser(user2).getFriends()) {
                if (userFirst.equals(userSecond)) {
                    mutualFriends.add(userStorage.getUser(userFirst));
                }
            }
        }
        return mutualFriends;
    }

    public List<User> getAllFriends(Integer id) {
        if (userStorage.getUser(id) == null) {
            throw new NotFoundException("пользователь не найден");
        }
        List<User> allFriends = new ArrayList<>();
        for (Integer idFriend : userStorage.getUser(id).getFriends()) {
            allFriends.add(userStorage.getUser(idFriend));
        }
        return allFriends;
    }
}
