package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> userMap;

    public void create(User user) {
        UserValidator.validate(user);
        userMap.put(user.getId(), user);
    }

    public void update(User user) {
        UserValidator.validate(user);
        userMap.put(user.getId(), user);
    }

    public void delete(User user) {
        userMap.remove(user.getId());
    }

    public User getUser(Integer id) {
        if (!userMap.containsValue(id)) {
            return null;
        } else {
           return userMap.get(id);
        }
    }
}
