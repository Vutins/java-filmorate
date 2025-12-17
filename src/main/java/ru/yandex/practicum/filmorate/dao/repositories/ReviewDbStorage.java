package ru.yandex.practicum.filmorate.dao.repositories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final ReviewRowMapper reviewRowMapper;

    @Override
    public Review create(Review review) {
        log.info("Создание отзыва в БД: {}", review);

        final String CHECK_EXISTS_QUERY = """
            SELECT COUNT(*) FROM reviews 
            WHERE user_id = ? AND film_id = ?
        """;

        Integer count = jdbcTemplate.queryForObject(
                CHECK_EXISTS_QUERY,
                Integer.class,
                review.getUserId(),
                review.getFilmId()
        );

        if (count != null && count > 0) {
            log.error("Пользователь {} уже оставлял отзыв фильму {}",
                    review.getUserId(), review.getFilmId());
            throw new ReviewAlreadyExistsException(
                    "Пользователь уже оставлял отзыв этому фильму."
            );
        }

        final String ADD_QUERY = """
            INSERT INTO reviews (content, is_positive, user_id, film_id, useful)
            VALUES (?, ?, ?, ?, 0)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        ADD_QUERY,
                        new String[]{"review_id"}
                );
                ps.setString(1, review.getContent());
                ps.setBoolean(2, review.getIsPositive());
                ps.setLong(3, review.getUserId());
                ps.setLong(4, review.getFilmId());
                return ps;
            }, keyHolder);

            Number key = keyHolder.getKey();
            if (key != null) {
                review.setId(key.longValue());
                log.info("Отзыв создан с ID: {}", review.getId());
            } else {
                throw new RuntimeException("Не удалось получить ID созданного отзыва");
            }

        } catch (DuplicateKeyException e) {
            log.error("Нарушение уникальности при создании отзыва: {}", e.getMessage());
            throw new ReviewAlreadyExistsException("Пользователь уже оставлял отзыв этому фильму.");
        } catch (DataAccessException e) {
            log.error("Ошибка доступа к данным при создании отзыва: {}", e.getMessage());
            throw e;
        }
        return review;
    }

    @Override
    public Review update(Review review) {
        log.info("Обновление отзыва с ID {} в БД", review.getId());

        final String UPDATE_QUERY = """
            UPDATE reviews
            SET content = ?,
                is_positive = ?
            WHERE review_id = ?
        """;

        int rowsUpdated = jdbcTemplate.update(
                UPDATE_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getId()
        );

        if (rowsUpdated == 0) {
            log.error("Отзыв с ID {} не найден для обновления", review.getId());
            throw new NotFoundException("Отзыв с ID " + review.getId() + " не найден");
        }

        // Возвращаем обновленный отзыв
        return findById(review.getId()).orElseThrow(
                () -> new NotFoundException("Отзыв не найден после обновления")
        );
    }

    @Override
    public void delete(long id) {
        log.info("Удаление отзыва с ID: {}", id);

        final String DELETE_QUERY = """
            DELETE FROM reviews
            WHERE review_id = ?
        """;

        int rowsDeleted = jdbcTemplate.update(DELETE_QUERY, id);
        log.info("Удалено отзывов: {}", rowsDeleted);
    }

    @Override
    public Optional<Review> findById(long id) {
        log.debug("Поиск отзыва по ID: {}", id);

        final String FIND_BY_ID_QUERY = """
            SELECT *
            FROM reviews
            WHERE review_id = ?
        """;

        try {
            return jdbcTemplate.query(FIND_BY_ID_QUERY, reviewRowMapper, id)
                    .stream()
                    .findFirst();
        } catch (DataAccessException e) {
            log.error("Ошибка при поиске отзыва по ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Collection<Review> findAllByFilmId(Long filmId, int count) {
        log.debug("Поиск отзывов: filmId={}, count={}", filmId, count);

        final String GET_REVIEWS_SORTED_BY_USEFULNESS_QUERY = """
            SELECT *
            FROM reviews
            ORDER BY useful DESC
            LIMIT ?
        """;

        final String GET_REVIEWS_BY_FILM_SORTED_BY_USEFULNESS_QUERY = """
            SELECT *
            FROM reviews
            WHERE film_id = ?
            ORDER BY useful DESC
            LIMIT ?
        """;

        try {
            Collection<Review> reviews;
            if (filmId == null) {
                reviews = jdbcTemplate.query(
                        GET_REVIEWS_SORTED_BY_USEFULNESS_QUERY,
                        reviewRowMapper,
                        count
                );
                log.debug("Найдено {} отзывов (все фильмы)", reviews.size());
            } else {
                reviews = jdbcTemplate.query(
                        GET_REVIEWS_BY_FILM_SORTED_BY_USEFULNESS_QUERY,
                        reviewRowMapper,
                        filmId, count
                );
                log.debug("Найдено {} отзывов для фильма {}", reviews.size(), filmId);
            }
            return reviews;
        } catch (DataAccessException e) {
            log.error("Ошибка при поиске отзывов: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public void addReaction(long reviewId, long userId, boolean isLike) {
        log.info("Добавление реакции: reviewId={}, userId={}, isLike={}",
                reviewId, userId, isLike);

        try {
            // Для PostgreSQL используем правильный синтаксис
            final String UPSERT_REACTION_QUERY = """
                INSERT INTO review_reactions (review_id, user_id, is_like)
                VALUES (?, ?, ?)
                ON CONFLICT (review_id, user_id) 
                DO UPDATE SET is_like = EXCLUDED.is_like
            """;

            int rowsAffected = jdbcTemplate.update(
                    UPSERT_REACTION_QUERY,
                    reviewId, userId, isLike
            );
            log.info("Реакция добавлена/обновлена, затронуто строк: {}", rowsAffected);

            updateUseful(reviewId);

        } catch (DataAccessException e) {
            log.error("Ошибка при добавлении реакции: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при обработке реакции", e);
        }
    }

    @Override
    public void removeReaction(long reviewId, long userId, boolean isLike) {
        log.info("Удаление реакции: reviewId={}, userId={}, isLike={}",
                reviewId, userId, isLike);

        final String DELETE_REACTION_QUERY = """
            DELETE FROM review_reactions
            WHERE review_id = ?
            AND user_id = ?
            AND is_like = ?
        """;

        try {
            int rowsDeleted = jdbcTemplate.update(
                    DELETE_REACTION_QUERY,
                    reviewId, userId, isLike
            );

            if (rowsDeleted > 0) {
                log.info("Реакция удалена, затронуто строк: {}", rowsDeleted);
                updateUseful(reviewId);
            } else {
                log.warn("Реакция не найдена для удаления");
            }

        } catch (DataAccessException e) {
            log.error("Ошибка при удалении реакции: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при удалении реакции", e);
        }
    }

    private void updateUseful(long reviewId) {
        log.debug("Обновление полезности для отзыва ID: {}", reviewId);

        final String UPDATE_USEFUL_QUERY = """
            UPDATE reviews
            SET useful = (
                SELECT COALESCE(
                    SUM(
                        CASE
                            WHEN is_like = true THEN 1
                            WHEN is_like = false THEN -1
                            ELSE 0
                        END
                    ),
                    0
                )
                FROM review_reactions
                WHERE review_id = ?
            )
            WHERE review_id = ?
        """;

        try {
            int rowsUpdated = jdbcTemplate.update(UPDATE_USEFUL_QUERY, reviewId, reviewId);
            log.debug("Полезность обновлена для отзыва {}, затронуто строк: {}",
                    reviewId, rowsUpdated);

            // Проверяем результат
            final String CHECK_USEFUL_QUERY =
                    "SELECT useful FROM reviews WHERE review_id = ?";
            Integer useful = jdbcTemplate.queryForObject(
                    CHECK_USEFUL_QUERY,
                    Integer.class,
                    reviewId
            );
            log.debug("Отзыв {} теперь имеет полезность: {}", reviewId, useful);

        } catch (DataAccessException e) {
            log.error("Ошибка при обновлении полезности: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при обновлении полезности", e);
        }
    }
}