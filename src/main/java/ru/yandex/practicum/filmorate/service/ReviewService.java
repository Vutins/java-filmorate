package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventService eventService;

    public Review create(Review review) {
        log.info("Создание нового отзыва: {}", review);
        if (review.getIsPositive() == null) {
            log.error("Поле isPositive не может быть null");
            throw new ValidationException("Поле isPositive обязательно");
        }
        validateUserAndFilm(review.getUserId(), review.getFilmId());
        Review createdReview = reviewStorage.create(review);
        log.info("Отзыв успешно создан с ID: {}", createdReview.getId());

        eventService.addEvent(createdReview.getUserId(), EventTypes.REVIEW, OperationTypes.ADD, createdReview.getId());
        log.info("Добавлено событие (add review) в ленту пользователя");

        return createdReview;
    }

    public Review update(Review review) {
        log.info("Обновление отзыва с ID: {}, новые данные: {}",
                review.getId(), review);
        validateReview(review.getId());

        Review updatedReview = reviewStorage.update(review);
        log.info("Отзыв с ID: {} успешно обновлен, полезность: {}",
                updatedReview.getId(), updatedReview.getUseful());

        eventService.addEvent(updatedReview.getUserId(), EventTypes.REVIEW, OperationTypes.UPDATE, updatedReview.getId());
        log.info("Добавлено событие (update review) в ленту пользователя");

        return updatedReview;
    }

    public void delete(long id) {
        Optional<Review> deletingReview = reviewStorage.findById(id);

        log.info("Удаление отзыва с ID: {}", id);
        reviewStorage.delete(id);
        log.info("Отзыв с ID: {} удален из хранилища", id);

        if (deletingReview.isPresent()) {
            Review review = deletingReview.get();
            eventService.addEvent(review.getUserId(), EventTypes.REVIEW, OperationTypes.REMOVE, review.getId());
            log.info("Добавлено событие (remove review) в ленту пользователя");
        }
    }

    public Review findById(long id) {
        log.debug("Поиск отзыва по ID: {}", id);
        Review review = reviewStorage.findById(id)
                .orElseThrow(() -> {
                    log.warn("Отзыв с ID {} не найден", id);
                    return new NotFoundException("Отзыв с ID " + id + " не найден");
                });

        log.debug("Отзыв с ID: {} найден, полезность: {}", id, review.getUseful());
        return review;
    }

    public Collection<Review> findAllByFilmId(Long filmId, int count) {
        if (filmId != null) {
            log.info("Поиск {} отзывов для фильма с ID: {}", count, filmId);
        } else {
            log.info("Поиск {} последних отзывов (все фильмы)", count);
        }
        Collection<Review> reviews = reviewStorage.findAllByFilmId(filmId, count);
        log.info("Найдено {} отзывов", reviews.size());

        return reviews;
    }

    public void addReaction(long reviewId, long userId, boolean isLike) {
        String reactionType = isLike ? "лайк" : "дизлайк";

        log.info("Добавление {} от пользователя {} к отзыву {}",
                reactionType, userId, reviewId);
        if (!userStorage.validUserId(userId)) {
            log.error("Пользователь с ID {} не найден", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        validateReview(reviewId);
        reviewStorage.addReaction(reviewId, userId, isLike);
        log.info("{} от пользователя {} успешно добавлен к отзыву {}",
                reactionType, userId, reviewId);
    }

    public Review addReactionAndGetReview(long reviewId, long userId, boolean isLike) {
        addReaction(reviewId, userId, isLike);
        return findById(reviewId);
    }

    public void removeReaction(long reviewId, long userId, boolean isLike) {
        String reactionType = isLike ? "лайка" : "дизлайка";
        log.info("Удаление {} от пользователя {} у отзыва {}",
                reactionType, userId, reviewId);

        if (!userStorage.validUserId(userId)) {
            log.error("Пользователь с ID {} не найден", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        validateReview(reviewId);
        reviewStorage.removeReaction(reviewId, userId, isLike);
        log.info("{} от пользователя {} успешно удален у отзыва {}",
                reactionType, userId, reviewId);
    }

    public Review removeReactionAndGetReview(long reviewId, long userId, boolean isLike) {
        removeReaction(reviewId, userId, isLike);
        return findById(reviewId);
    }

    private void validateUserAndFilm(Long userId, Long filmId) {
        log.debug("Проверка существования пользователя {} и фильма {}", userId, filmId);
        if (userStorage.getUserById(userId) == null) {
            log.error("Пользователь с ID {} не найден при создании отзыва", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        if (filmStorage.getFilmById(filmId) == null) {
            log.error("Фильм с ID {} не найден при создании отзыва", filmId);
            throw new NotFoundException("Фильм с ID " + filmId + " не найден");
        }
        log.debug("Пользователь {} и фильм {} существуют", userId, filmId);
    }

    private void validateReview(Long reviewId) {
        log.debug("Проверка существования отзыва с ID: {}", reviewId);
        if (reviewStorage.findById(reviewId).isEmpty()) {
            log.error("Отзыв с ID {} не найден", reviewId);
            throw new NotFoundException("Отзыв с ID " + reviewId + " не найден");
        }
        log.debug("Отзыв с ID: {} существует", reviewId);
    }
}