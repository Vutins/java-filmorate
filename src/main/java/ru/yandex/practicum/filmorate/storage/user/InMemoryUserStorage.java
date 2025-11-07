package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final HashMap<Integer, User> userMap = new HashMap<>();
    private Integer id = 1;

    public User create(User user) {
        user.setId(id);
        userMap.put(id++, user);
        return user;
    }

    public User update(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    public void delete(Integer id) {
        userMap.remove(id);
    }

    public User getUser(Integer id) {
        if (!userMap.containsKey(id)) {
            return null;
        } else {
           return userMap.get(id);
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    public List<Integer> getAllKeys() {
        return new ArrayList<>(userMap.keySet());
    }
}
