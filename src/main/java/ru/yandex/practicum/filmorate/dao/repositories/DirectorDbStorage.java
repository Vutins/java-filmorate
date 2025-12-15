package ru.yandex.practicum.filmorate.dao.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Slf4j
@Repository
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Director> directorRowMapper;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate, RowMapper<Director> directorRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.directorRowMapper = directorRowMapper;
    }

    @Override
    public Director create(Director director) {
        log.debug("DAO: Creating director {}", director);
        final String sql = "INSERT INTO directors (name) VALUES (?)";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);
        director.setId(keyHolder.getKey().longValue());
        return director;
    }

    @Override
    public int update(Director director) {
        log.debug("DAO: Updating director {}", director);
        final String sql = "UPDATE directors SET name = ? WHERE director_id = ?";

        int updated = jdbcTemplate.update(sql, director.getName(), director.getId());

        return updated;
    }

    @Override
    public int delete(Long id) {
        log.debug("DAO: Deleting director {}", id);
        final String sql = "DELETE FROM directors WHERE director_id = ?";
        int deleted = jdbcTemplate.update(sql, id);

        return deleted;
    }

    @Override
    public Optional<Director> getById(Long id) {
        log.debug("DAO: Getting director");
        final String sql = "SELECT * FROM directors WHERE director_id = ?";
        try {
            Director result = jdbcTemplate.queryForObject(sql, directorRowMapper, id);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Director> getAll() {
        log.debug("DAO: getAll directors");
        final String sql = "SELECT * FROM directors ORDER BY director_id ASC";
        List<Director> result = jdbcTemplate.query(sql, directorRowMapper);
        return result;
    }

    @Override
    public void addDirectorToFilm(Film film) {
        log.debug("DAO: Adding director to film");
        final String sql = "INSERT INTO film_directors (director_id, film_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sql, film.getDirectors(), film.getDirectors().size(), ((ps, director) -> {
            ps.setLong(1, director.getId());
            ps.setLong(2, film.getId());
        }));
    }

    @Override
    public int deleteDirectorsFromFilm(Long filmId) {
        log.debug("DAO: Deleting film directors from film");
        final String sql = "DELETE FROM film_directors WHERE film_id = ?";
        int result = jdbcTemplate.update(sql, filmId);

        return result;
    }

    @Override
    public Set<Director> getDirectorsByFilmId(Long filmId) {
        log.debug("DAO: getDirectorsByFilmId");
        final String sql = """
                SELECT d.director_id, d.name
                FROM directors AS d
                JOIN film_directors AS fd ON d.director_id = fd.director_id
                WHERE fd.film_id = ?
                ORDER BY d.director_id ASC""";
        List<Director> result = jdbcTemplate.query(sql, directorRowMapper, filmId);
        return new LinkedHashSet<>(result);
    }

    @Override
    public Map<Long, Set<Director>> getAllDirectorsByFilm() {
        log.debug("DAO: getAllDirectorsByFilm");
        final String sql = """
                SELECT *
                FROM (SELECT f.id, d.director_id, d.name
                 FROM films f
                 JOIN film_directors fd ON f.id = fd.film_id
                 JOIN directors d ON fd.director_id = d.director_id) AS s
                 ORDER BY s.id ASC, s.director_id ASC""";
        Map<Long, Set<Director>> result = jdbcTemplate.query(sql, new ResultSetExtractor<Map<Long, Set<Director>>>() {
            @Override
            public Map<Long, Set<Director>> extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<Long, Set<Director>> result = new LinkedHashMap<>();

                while (rs.next()) {
                    Long filmId = rs.getLong("id");

                    if (filmId != null) {
                        Director director = Director.builder().id(rs.getLong("director_id")).name(rs.getString("name")).build();

                        if (!result.containsKey(filmId)) {
                            Set<Director> directors = new LinkedHashSet<>();
                            directors.add(director);
                            result.put(filmId, directors);
                        } else {
                            result.get(filmId).add(director);
                        }
                    }
                }

                return result;
            }
        });

        return result;
    }

    public List<Director> searchDirectorsByName(String query) {
        final String sql = """
        SELECT * FROM directors
        WHERE LOWER(name) LIKE ?
        ORDER BY director_id ASC
        """;
        return jdbcTemplate.query(sql, directorRowMapper, "%" + query.toLowerCase() + "%");
    }
}
