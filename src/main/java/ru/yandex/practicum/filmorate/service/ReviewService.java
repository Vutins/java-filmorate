package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;

    public Review create(Review review) {
        log.info("Создание нового отзыва пользователем {} для фильма {}",
                review.getUserId(), review.getFilmId());

        validateUserAndFilm(review.getUserId(), review.getFilmId());

        Review createdReview = reviewStorage.create(review);
        log.info("Отзыв успешно создан с ID: {}, полезность: {}",
                createdReview.getId(), createdReview.getUseful());

        return createdReview;
    }

    public Review update(Review review) {
        log.info("Обновление отзыва с ID: {}, новые данные: {}",
                review.getId(), review);

        validateReview(review.getId());

        Review updatedReview = reviewStorage.update(review);
        log.info("Отзыв с ID: {} успешно обновлен, полезность: {}",
                updatedReview.getId(), updatedReview.getUseful());

        return updatedReview;
    }

    public void delete(long id) {
        log.info("Удаление отзыва с ID: {}", id);

        reviewStorage.delete(id);
        log.info("Отзыв с ID: {} удален из хранилища", id);
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

        userService.validUserId(userId);

        if (!userService.validUserId(userId)) {
            log.error("Пользователь с ID {} не найден", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        validateReview(reviewId);
        reviewStorage.addReaction(reviewId, userId, isLike);
        log.info("{} от пользователя {} успешно добавлен к отзыву {}",
                reactionType, userId, reviewId);
    }

    public void removeReaction(long reviewId, long userId, boolean isLike) {
        String reactionType = isLike ? "лайка" : "дизлайка";
        log.info("Удаление {} от пользователя {} у отзыва {}",
                reactionType, userId, reviewId);

        userService.validUserId(userId);
        validateReview(reviewId);

        reviewStorage.removeReaction(reviewId, userId, isLike);
        log.info("{} от пользователя {} успешно удален у отзыва {}",
                reactionType, userId, reviewId);
    }

    private void validateUserAndFilm(Long userId, Long filmId) {
        log.debug("Проверка существования пользователя {} и фильма {}", userId, filmId);

        if (userService.getUserById(userId) == null) {
            log.error("Пользователь с ID {} не найден при создании отзыва", userId);
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }

        if (filmService.getFilmById(filmId) == null) {
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