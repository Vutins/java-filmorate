package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private final Integer id;
    @Email
    private String email;
    @NotBlank
    private String login;
    @NotBlank
    private String name;
    @PastOrPresent
    private LocalDate birthday;
}
