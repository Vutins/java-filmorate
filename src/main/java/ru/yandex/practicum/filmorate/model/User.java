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
    private Long id;
    private String name;
    @Email
    @NotEmpty
    @NotBlank
    private String email;
    @NotBlank
    @NotNull
    private String login;
    @Past
    private LocalDate birthday;
}