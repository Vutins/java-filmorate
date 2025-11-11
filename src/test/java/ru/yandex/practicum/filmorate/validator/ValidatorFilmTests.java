package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashSet;

public class ValidatorFilmTests {

    @Test
    void shouldNotThrowExceptionForDescription() {
        Film film1 = new Film(1, "film1"
                , "\"Дом Кобб – вор, который крадет секреты из подсознания."
                + " Ему предлагают невыполнимую задачу – внедрить идею в сознание человека."
                + " Если он преуспеет, это станет идеальным преступлением. Но враги повсюду,",
                LocalDate.now(), 100, new HashSet<>());
        System.out.println("длина описание фильма 1: " + film1.getDescription().length());
        FilmValidator.validate(film1);

        Film film2 = new Film(2, "film2",
                "Энди Дюфрейн, невинно осужденный за убийство жены, попадает в тюрьму Шоушенк."
                + " За 20 лет он находит неожиданные способы выжить и сохранить надежду."
                + " Его дружба с Редом и тихая борьба с системой меняют ж",
                LocalDate.now(), 200, new HashSet<>());
        System.out.println("длина описание фильма 2: " + film2.getDescription().length());
        FilmValidator.validate(film2);
    }

    @Test
    void shouldNotThrowExceptionForRelease() {
        Film film1 = new Film(1, "film1",
                "\"Дом Кобб – вор, который крадет секреты из подсознания."
                + " Ему предлагают невыполнимую задачу – внедрить идею в сознание человека."
                + " Если он преуспеет, это станет идеальным преступлением. Но враги повсюду,",
                LocalDate.of(1985, 12, 28), 100, new HashSet<>());
        FilmValidator.validate(film1);
    }
}