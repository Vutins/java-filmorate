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
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

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
            INSERT INTO reviews (content, is_positive, user_id, film_id)
            VALUES (?, ?, ?, ?)
        """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        ADD_QUERY,
                        new String[]{"review_id"}
                );
                ps.setString(1, review.getContent());
                ps.setBoolean(2, review.isPositive());
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
                review.isPositive(),
                review.getId()
        );

        if (rowsUpdated == 0) {
            log.error("Отзыв с ID {} не найден для обновления", review.getId());
            throw new NotFoundException("Отзыв с ID " + review.getId() + " не найден");
        }
        log.info("Отзыв с ID {} обновлен, затронуто строк: {}", review.getId(), rowsUpdated);
        return review;
    }

    @Override
    public void delete(long id) {
        final String DELETE_QUERY = """
            DELETE FROM reviews
            WHERE review_id = ?
        """;

        jdbcTemplate.update(DELETE_QUERY, id);
    }

    @Override
    public Optional<Review> findById(long id) {
         final String FIND_BY_ID_QUERY = """
            SELECT *
            FROM reviews
            WHERE review_id = ?
        """;

        return jdbcTemplate.query(FIND_BY_ID_QUERY, new ReviewRowMapper(), id).stream().findFirst();
    }

    @Override
    public Collection<Review> findAllByFilmId(Long filmId, int count) {
        final String GET_REVIEWS_SORTED_BY_USEFULNESS_QUERY = """
            SELECT *
            FROM reviews useful DESC
            LIMIT ?
        """;

        final String GET_REVIEWS_BY_FILM_SORTED_BY_USEFULNESS_QUERY = """
            SELECT *
            FROM reviews
            WHERE film_id = ?
            ORDER BY useful DESC
            LIMIT ?
        """;

        String sql = (filmId == null)
                ? GET_REVIEWS_SORTED_BY_USEFULNESS_QUERY
                : GET_REVIEWS_BY_FILM_SORTED_BY_USEFULNESS_QUERY;
        return (filmId == null)
                ? jdbcTemplate.query(sql, new ReviewRowMapper(), count)
                : jdbcTemplate.query(sql, new ReviewRowMapper(), filmId, count);
    }

    @Override
    public void addReaction(long reviewId, long userId, boolean isLike) {
        final String INSERT_OR_UPDATE_REVIEW_REACTION_QUERY = """
            INSERT INTO review_reactions (review_id, user_id, is_like)
            VALUES (?, ?, ?) ON CONFLICT (review_id, user_id) DO
            UPDATE SET is_like = EXCLUDED.is_like
        """;

        jdbcTemplate.update(INSERT_OR_UPDATE_REVIEW_REACTION_QUERY, reviewId, userId, isLike);
        updateUseful(reviewId);
    }

    @Override
    public void removeReaction(long reviewId, long userId, boolean isLike) {
        final String DELETE_REVIEW_REACTIONS_QUERY = """
            DELETE FROM review_reactions
            WHERE review_id = ?
            AND user_id = ?
            AND is_like = ?
        """;

        jdbcTemplate.update(DELETE_REVIEW_REACTIONS_QUERY, reviewId, userId, isLike);
        updateUseful(reviewId);
    }

    private void updateUseful(long reviewId) {
        final String UPDATE_REVIEW_USEFULNESS_QUERY = """
            UPDATE reviews
            SET useful = (
                    SELECT
                        COALESCE(
                            SUM(
                                CASE
                                    WHEN is_like THEN 1
                                    ELSE -1
                                END
                            ),
                            0
                        )
                    FROM review_reactions
                    WHERE review_id = ?
                )
            WHERE review_id = ?
        """;

        jdbcTemplate.update(UPDATE_REVIEW_USEFULNESS_QUERY, reviewId, reviewId);
    }
}
