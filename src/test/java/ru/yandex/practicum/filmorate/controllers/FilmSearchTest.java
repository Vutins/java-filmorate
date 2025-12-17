package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dao.mappers.*;
import ru.yandex.practicum.filmorate.dao.repositories.*;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, UserDbStorage.class, UserRowMapper.class,
        DirectorDbStorage.class, DirectorRowMapper.class, FilmService.class,
        EventService.class, EventRowMapper.class, EventDbStorage.class})
public class FilmSearchTest {

    private final FilmDbStorage filmDbStorage;
    private final DirectorDbStorage directorDbStorage;
    private final FilmService filmService;

    private Director director1;
    private Director director2;
    private Director director3;

    @BeforeEach
    void setUp() {
        // Создаем режиссеров
        director1 = Director.builder().name("Кристофер Нолан").build();
        director2 = Director.builder().name("Квентин Тарантино").build();
        director3 = Director.builder().name("Джеймс Кэмерон").build();

        director1 = directorDbStorage.create(director1);
        director2 = directorDbStorage.create(director2);
        director3 = directorDbStorage.create(director3);

        // Создаем фильмы
        Rating mpa = new Rating(1, "G");

        Film film1 = new Film(0L, "Начало", "Описание 1", LocalDate.of(2010, 1, 1), 148,
                Set.of(new Genre(1, "Комедия")), mpa, Set.of(director1));
        Film film2 = new Film(0L, "Криминальное чтиво", "Описание 2", LocalDate.of(1994, 1, 1), 154,
                Set.of(new Genre(2, "Драма")), mpa, Set.of(director2));
        Film film3 = new Film(0L, "Титаник", "Описание 3", LocalDate.of(1997, 1, 1), 195,
                Set.of(new Genre(3, "Мультфильм")), mpa, Set.of(director3));
        Film film4 = new Film(0L, "Крадущийся тигр", "Описание 4", LocalDate.of(2000, 1, 1), 120,
                Set.of(new Genre(4, "Триллер")), mpa, new HashSet<>());

        // Создаем фильмы через сервис, чтобы сохранились режиссеры
        filmService.create(film1);
        filmService.create(film2);
        filmService.create(film3);
        filmService.create(film4);
    }

    @Test
    @DirtiesContext
    public void testSearchByTitle() {
        System.out.println("Поиск по запросу 'тит' в названии...");
        List<Film> result = filmDbStorage.searchFilms("тит", List.of("title"));

        System.out.println("Найдено фильмов: " + result.size());
        result.forEach(f -> System.out.println("  Found: " + f.getName()));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Титаник");
    }

    @Test
    @DirtiesContext
    public void testSearchByTitleWithCommonSubstring() {
        System.out.println("Поиск по запросу 'ти' в названии...");
        List<Film> result = filmDbStorage.searchFilms("ти", List.of("title"));

        System.out.println("Найдено фильмов: " + result.size());
        result.forEach(f -> System.out.println("  Found: " + f.getName()));

        List<String> filmNames = result.stream().map(Film::getName).toList();
        assertThat(filmNames).contains("Титаник", "Крадущийся тигр", "Криминальное чтиво");
        assertThat(result).hasSize(3);
    }

    @Test
    @DirtiesContext
    public void testSearchByDirector() {
        System.out.println("Поиск по запросу 'нолан' в режиссерах...");
        List<Film> result = filmDbStorage.searchFilms("нолан", List.of("director"));

        System.out.println("Найдено фильмов: " + result.size());
        result.forEach(f -> System.out.println("  Found: " + f.getName()));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Начало");
    }

    @Test
    @DirtiesContext
    public void testSearchByBoth() {
        System.out.println("Поиск по запросу 'кри' в названии и режиссерах...");
        List<Film> result = filmDbStorage.searchFilms("кри", List.of("title", "director"));

        System.out.println("Найдено фильмов: " + result.size());
        result.forEach(f -> System.out.println("  Found: " + f.getName()));

        List<String> filmNames = result.stream().map(Film::getName).toList();
        assertThat(filmNames).contains("Начало", "Криминальное чтиво");
        assertThat(result).hasSize(2);
    }

    @Test
    @DirtiesContext
    public void testSearchWithEmptyResult() {
        System.out.println("Поиск по несуществующему запросу...");
        List<Film> result = filmDbStorage.searchFilms("несуществующий", List.of("title", "director"));

        assertThat(result).isEmpty();
    }
}