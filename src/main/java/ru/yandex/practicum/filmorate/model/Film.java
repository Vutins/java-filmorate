package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.SequencedSet;
import java.util.Set;

@Data
@Value
@AllArgsConstructor
public class Film {

    Long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    LocalDate releaseDate;
    @NotNull
    Integer duration;
    Set<Integer> likes = new HashSet<>();
    @NotNull
    SequencedSet<Genre> genres;
    Rating mpa;
}
