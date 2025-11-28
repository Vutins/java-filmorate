package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    Long id;
    String name;
    @Email
    @NotEmpty
    @NotBlank
    String email;
    @NotBlank
    @NotNull
    String login;
    @Past
    LocalDate birthday;
}