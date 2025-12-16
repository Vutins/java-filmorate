package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Review create(@Valid @RequestBody Review review) {
        log.info("Запрос на создание нового отзыва: {}", review);
        Review createdReview = reviewService.create(review);
        log.info("Отзыв успешно создан с ID: {}", createdReview.getId());
        return createdReview;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Review update(@Valid @RequestBody Review review) {
        log.info("Запрос на обновление отзыва с ID: {}", review.getId());
        Review updatedReview = reviewService.update(review);
        log.info("Отзыв с ID: {} успешно обновлен", updatedReview.getId());
        return updatedReview;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable long id) {
        log.info("Запрос на удаление отзыва с ID: {}", id);
        reviewService.delete(id);
        log.info("Отзыв с ID: {} успешно удален", id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Review findById(@PathVariable long id) {
        log.info("Запрос на получение отзыва с ID: {}", id);
        Review review = reviewService.findById(id);
        log.info("Отзыв с ID: {} успешно получен", id);
        return review;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Review> findAllByFilmId(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10") int count) {
        if (filmId != null) {
            log.info("Запрос на получение {} отзывов для фильма с ID: {}", count, filmId);
        } else {
            log.info("Запрос на получение {} последних отзывов (все фильмы)", count);
        }
        Collection<Review> reviews = reviewService.findAllByFilmId(filmId, count);
        log.info("Найдено {} отзывов", reviews.size());
        return reviews;
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Запрос на добавление лайка от пользователя {} к отзыву {}", userId, id);
        reviewService.addReaction(id, userId, true);
        log.info("Лайк от пользователя {} успешно добавлен к отзыву {}", userId, id);
    }

    @PutMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addDislike(@PathVariable long id, @PathVariable long userId) {
        log.info("Запрос на добавление дизлайка от пользователя {} к отзыву {}", userId, id);
        reviewService.addReaction(id, userId, false);
        log.info("Дизлайк от пользователя {} успешно добавлен к отзыву {}", userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Запрос на удаление лайка от пользователя {} к отзыву {}", userId, id);
        reviewService.removeReaction(id, userId, true);
        log.info("Лайк от пользователя {} успешно удален у отзыва {}", userId, id);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeDislike(@PathVariable long id, @PathVariable long userId) {
        log.info("Запрос на удаление дизлайка от пользователя {} к отзыву {}", userId, id);
        reviewService.removeReaction(id, userId, false);
        log.info("Дизлайк от пользователя {} успешно удален у отзыва {}", userId, id);
    }
}