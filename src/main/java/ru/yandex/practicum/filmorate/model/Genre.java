package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    private Integer id;
    private String name;
}