package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingStorage mpaRatingStorage;

    public List<Rating> getAllRatings() {
        return List.copyOf(mpaRatingStorage.getAllRatings());
    }

    public Rating getRatingById(Integer id) {
        if (id == null) {
            throw new ValidationException("Рейтинг не может быть получен по ID = null");
        }
        return mpaRatingStorage.getRatingById(id);
    }
}