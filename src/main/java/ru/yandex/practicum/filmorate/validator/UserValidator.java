package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class UserValidator {

    public static void validate(User user) {
        log.info("валидация данных пользователя");
        validateEmail(user.getEmail());
        validateLogin(user.getLogin());
        validateName(user);
        validateBirthday(user.getBirthday());
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("электронная почта не может быть пустой");
        }

        if (!email.contains("@")) {
            throw new ValidationException("электронная почта должна содержать символ @");
        }
    }

    private static void validateLogin(String login) {
        if (login.isBlank() || login.contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
    }

    private static void validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private static void validateBirthday(LocalDate birthday) {
        if (birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}