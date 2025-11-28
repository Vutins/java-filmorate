package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    @Id
    private Long id;
    @NotBlank
    @NotNull
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    @PastOrPresent(message = "Дата не может быть в будущем")
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Set<Genre> genres;
    private Rating mpa;
}