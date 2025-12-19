package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.GenreValueList;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;
import ru.yandex.practicum.filmorate.model.enums.RatingValueList;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ValidationTool;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {

    private static final String PROGRAM_LEVEL = "FilmService";
    private final EventService eventService;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;
    private final GenreService genreService;

    public void delete(Long filmId) {
        String message;

        if (filmId == null || filmId < 1) {
            message = String.format("%s : Попытка удалить фильм по ID = %s", PROGRAM_LEVEL, filmId);
            log.warn(message);
            throw new ValidationException(message);
        }

        try {
            filmStorage.getFilmById(filmId);
        } catch (NotFoundException e) {
            message = String.format("%s : Фильм с ID = %s не найден в приложении", PROGRAM_LEVEL, filmId);
            log.warn(message);
            throw new NotFoundException(message);
        }

        boolean isDeleted = filmStorage.delete(filmId);

        if (!isDeleted) {
            message = String.format("%s : Не удалось удалить фильм с ID = %s", PROGRAM_LEVEL, filmId);
            log.error(message);
            throw new ValidationException(message);
        }
        message = String.format("%s : Фильм ID %s успешно удален", PROGRAM_LEVEL, filmId);
        log.info(message);
    }

    public List<Film> getAllFilms() {
        List<Film> allFilms = filmStorage.getAllFilms();
        addProducersToFilms(allFilms);
        return allFilms;
    }

    public Film getFilmById(Long id) {
        if (id == null) {
            log.warn(PROGRAM_LEVEL + ": Запрос на получение фильма по ID = null");
            throw new ValidationException(PROGRAM_LEVEL + ": Фильм не может быть получен по ID = null");
        }
        Film film = filmStorage.getFilmById(id);
        Set<Director> directors = directorStorage.getDirectorsByFilmId(id);
        film.setDirectors(directors);
        log.info(PROGRAM_LEVEL + ": Объект Film успешно найден по ID");
        return film;
    }

    public Film create(Film film) {
        ValidationTool.filmCheck(film, PROGRAM_LEVEL);

        Set<Genre> validGenresSet = new LinkedHashSet<>();
        if ((film.getGenres() != null) && !(film.getGenres().isEmpty())) {
            for (Genre genre : film.getGenres()) {
                if ((genre.getId() < 1) || (genre.getId() > GenreValueList.values().length)) {
                    throw new NotFoundException(PROGRAM_LEVEL + ": Жанр с ID: " + genre.getId() + " не найден в приложении");
                }
                if ((genre.getName() != null) && (GenreValueList.isCorrectGenre(genre.getName()))) {
                    validGenresSet.add(genre);
                } else {
                    Genre validGenre = new Genre(genre.getId(), GenreValueList.values()[genre.getId() - 1].getGenre());
                    validGenresSet.add(validGenre);
                }
            }
        }

        Rating validFilmRating;
        if (film.getMpa() == null) {
            validFilmRating = new Rating(1, RatingValueList.values()[0].getRating()); // Это работает!
        } else {
            if ((film.getMpa().getId() < 1) || (film.getMpa().getId() > RatingValueList.values().length)) {
                throw new NotFoundException("FilmDbService: MPA рейтинг с ID: " + film.getMpa().getId() + " не найден в приложении");
            }
            if ((film.getMpa().getName() != null) && (RatingValueList.isCorrectRating(film.getMpa().getName()))) {
                validFilmRating = film.getMpa();
            } else {
                validFilmRating = new Rating(film.getMpa().getId(), RatingValueList.values()[film.getMpa().getId() - 1].getRating());
            }
        }

        // Сортируем жанры по id перед использованием
        Set<Genre> sortedGenres = validGenresSet.stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Film validFilm = new Film(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), sortedGenres, validFilmRating, film.getDirectors());
        Film filmResult = filmStorage.create(validFilm);
        validateDirectorExists(filmResult);
        addDirectorToFilm(filmResult);
        return filmResult;
    }

    public Film update(Film film) {
        ValidationTool.filmCheck(film, PROGRAM_LEVEL);

        getFilmById(film.getId());

        Set<Genre> validGenresSet = new LinkedHashSet<>();
        if ((film.getGenres() != null) && !(film.getGenres().isEmpty())) {
            for (Genre genre : film.getGenres()) {
                if ((genre.getId() < 1) || (genre.getId() > GenreValueList.values().length)) {
                    throw new NotFoundException("FilmDbService: Жанр с ID: " + genre.getId() + " не найден в приложении");
                }
                if ((genre.getName() != null) && (GenreValueList.isCorrectGenre(genre.getName()))) {
                    validGenresSet.add(genre);
                } else {
                    Genre validGenre = new Genre(genre.getId(), GenreValueList.values()[genre.getId() - 1].getGenre());
                    validGenresSet.add(validGenre);
                }
            }
        }

        Rating validFilmRating;
        if (film.getMpa() == null) {
            validFilmRating = new Rating(1, RatingValueList.values()[0].getRating());
        } else {
            if ((film.getMpa().getId() < 1) || (film.getMpa().getId() > RatingValueList.values().length)) {
                throw new NotFoundException("FilmDbService: MPA рейтинг с ID: " + film.getMpa().getId() + " не найден в приложении");
            }
            if ((film.getMpa().getName() != null) && (RatingValueList.isCorrectRating(film.getMpa().getName()))) {
                validFilmRating = film.getMpa();
            } else {
                validFilmRating = new Rating(film.getMpa().getId(), RatingValueList.values()[film.getMpa().getId() - 1].getRating());
            }
        }

        validateDirectorExists(film);
        deleteDirectorsFromFilm(film);
        addDirectorToFilm(film);

        // Сортируем жанры по id перед использованием
        Set<Genre> sortedGenres = validGenresSet.stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Film validFilm = new Film(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), sortedGenres, validFilmRating, film.getDirectors());

        return filmStorage.update(validFilm);
    }

    public void addLike(Long filmId, Long userId) {
        ValidationTool.checkForNull(filmId, PROGRAM_LEVEL, "Лайк к фильму не может быть добален по ID фильма = null");

        ValidationTool.checkForNull(userId, PROGRAM_LEVEL, "Лайк к фильму не может быть добален по ID пользователя = null");

        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);

        filmStorage.addLike(filmId, userId);
        log.info("Лайк фильму успешно добавлен");
        eventService.addEvent(userId, EventTypes.LIKE, OperationTypes.ADD, filmId);
        log.info("{}: Добавлено событие (add like) в ленту пользователя", PROGRAM_LEVEL);
    }

    public void removeLike(Long filmId, Long userId) {
        ValidationTool.checkForNull(filmId, PROGRAM_LEVEL, "Лайк у фильма не может быть удален по ID" + " фильма = null");
        ValidationTool.checkForNull(userId, PROGRAM_LEVEL, "Лайк у фильма не может быть удален по ID" + " пользователя = null");

        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);

        filmStorage.removeLike(filmId, userId);
        log.info("Лайк фильма успешно удален");
        eventService.addEvent(userId, EventTypes.LIKE, OperationTypes.REMOVE, filmId);
        log.info("{}: Добавлено событие (remove like) в ленту пользователя", PROGRAM_LEVEL);
    }

    public List<Film> getPopularFilms(int limit, Integer genreId, Integer year) {
        if (limit <= 0) {
            return List.of();
        }
        if (genreId != null) {
            Genre genre = genreService.getGenreById(genreId);
            if (genre == null) {
                throw new NotFoundException("Жанр с ID: " + genreId + " не найден!");
            }
        }

        List<Film> films = filmStorage.getTopFilms(limit, genreId, year);
        addProducersToFilms(films);

        return List.copyOf(films);
    }

    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        Optional<Director> director = directorStorage.getById(directorId);
        if (!director.isPresent()) {
            throw new NotFoundException("Режиссёр с id=" + directorId + " не найден");
        }

        List<Film> films = filmStorage.getFilmsByDirector(directorId, sortBy);
        if (films.isEmpty()) {
            return films;
        }

        addProducersToFilms(films);
        return films;
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        if (!userStorage.validUserId(userId) || !userStorage.validUserId(friendId)) {
            throw new NotFoundException("пользователь с таким ID не существует - getCommonFilms");
        }
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> searchFilms(String query, List<String> by) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        if (by == null || by.isEmpty()) {
            by = List.of("title", "director");
        }

        List<String> validByValues = new ArrayList<>();
        for (String searchType : by) {
            if ("title".equals(searchType) || "director".equals(searchType)) {
                validByValues.add(searchType);
            }
        }

        if (validByValues.isEmpty()) {
            validByValues = List.of("title", "director");
        }

        List<Film> films = filmStorage.searchFilms(query, validByValues);
        addProducersToFilms(films);
        return films;
    }

    private Film validateDirectorExists(Film film) {
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            List<Director> directors = directorStorage.getAll().stream().filter(director -> film.getDirectors().contains(director)).toList();
            if (directors.isEmpty()) {
                throw new NotFoundException("Такого режиссёра не существует");
            }
            film.setDirectors(new LinkedHashSet<>(directors));
            return film;
        }
        return film;
    }

    private void addDirectorToFilm(Film film) {
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            directorStorage.addDirectorToFilm(film);
        }
    }

    private void deleteDirectorsFromFilm(Film film) {
        directorStorage.deleteDirectorsFromFilm(film.getId());
    }

    private void addProducersToFilms(List<Film> films) {
        if (films != null && !films.isEmpty()) {
            Map<Long, Set<Director>> directors = directorStorage.getAllDirectorsByFilm();

            for (Film film : films) {
                if (directors.containsKey(film.getId())) {
                    film.setDirectors(directors.get(film.getId()));
                }
            }
        }
    }
}