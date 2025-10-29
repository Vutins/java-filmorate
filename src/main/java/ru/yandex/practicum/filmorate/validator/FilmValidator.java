package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmValidator {

    private final static int  MAX_LENGTH_DESCRIPTION = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1985, 12, 28);

    public static void validate(Film film) {
        validateName(film.getName());
        validateDescription(film.getDescription());
        validateRelease(film.getRelease());
        validateDuration(film.getDuration());
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw  new ValidationException("Название фильма не может быть пустым");
        }
    }

    private static void validateDescription(String description) {
        if (description != null && description.length() > MAX_LENGTH_DESCRIPTION) {
            throw new ValidationException("Превышена длина описание фильма. Максимальная длина - "
                    + MAX_LENGTH_DESCRIPTION + " символов.");
        }
    }

    private static void validateRelease(LocalDate release) {
        if (release == null || release.isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше " + MIN_RELEASE_DATE);
        }
    }

    private static void validateDuration(Integer duration) {
        if (duration <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
