package ru.yandex.practicum.filmorate.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ErrorResponse {

    String error;
    String description;
}
