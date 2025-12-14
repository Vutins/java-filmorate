package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dao.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.dao.repositories.DirectorDbStorage;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DirectorDbStorage.class, DirectorRowMapper.class})
public class DirectorControllerTest {
    @Autowired
    private DirectorDbStorage directorDbStorage;

    private Director director;
    private Director director2;

    @BeforeEach
    void setUp() {
        director = Director.builder().name("Director 1").build();
        director2 = Director.builder().name("Director 2").build();
    }

    @Test
    void createDirector() {
        // Подготовка
        Director directorCreate = directorDbStorage.create(director);
        Director directorCreate2 = directorDbStorage.create(director2);

        // Исполнение
        List<Director> directors = directorDbStorage.getAll();

        // Проверка
        assertThat(directors.size()).isEqualTo(2);
        assertThat(directorCreate.getName()).isEqualTo(directors.get(0).getName());
        assertThat(directorCreate.getId()).isEqualTo(directors.get(0).getId());
        assertThat(directorCreate2.getName()).isEqualTo(directors.get(1).getName());
        assertThat(directorCreate2.getId()).isEqualTo(directors.get(1).getId());
    }

    @Test
    void updateDirector() {
        // Подготовка
        Director directorCreate = directorDbStorage.create(director);
        Director directorUpdate = Director.builder().id(directorCreate.getId()).name("Update").build();

        // Исполнение
        directorDbStorage.update(directorUpdate);
        Optional<Director> result = directorDbStorage.getById(directorCreate.getId());

        // Проверка
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getName()).isEqualTo(directorUpdate.getName());
        assertThat(result.get().getId()).isEqualTo(directorCreate.getId());
    }

    @Test
    void deleteDirector() {
        // Подготовка
        Director directorCreate = directorDbStorage.create(director);
        Director directorCreate2 = directorDbStorage.create(director2);

        // Исполнение
        directorDbStorage.delete(directorCreate.getId());
        List<Director> directors = directorDbStorage.getAll();

        // Проверка
        assertThat(directors.size()).isEqualTo(1);
        assertThat(directors.get(0).getId()).isEqualTo(directorCreate2.getId());
    }

    @Test
    void getDirectorById() {
        // Подготовка
        Director directorCreate = directorDbStorage.create(director);

        // Исполнение
        Optional<Director> result = directorDbStorage.getById(directorCreate.getId());

        // Проверка
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getName()).isEqualTo(directorCreate.getName());
        assertThat(result.get().getId()).isEqualTo(directorCreate.getId());
    }
}
