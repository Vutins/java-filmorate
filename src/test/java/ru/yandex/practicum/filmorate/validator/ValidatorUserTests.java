package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;

public class ValidatorUserTests {

    @Test
    void shouldNotThrowExceptionForEmail() {
        User user = new User(1, "nik@.com", "login", "name",
                LocalDate.of(2004, 5, 21), new HashSet<>());
        UserValidator.validate(user);
    }

    @Test
    void shouldNotThrowExceptionForNameAndSetName() {
        User user = new User(1, "nik@.com", "login", "",
                LocalDate.of(2004, 5, 21), new HashSet<>());
        UserValidator.validate(user);
        System.out.println("имя пользователя: " + user.getName());
    }

    @Test
    void shouldNotExceptionForBirthday() {
        User user = new User(1, "nik@.com", "login", "name",
                LocalDate.now(), new HashSet<>());
        UserValidator.validate(user);
    }
}