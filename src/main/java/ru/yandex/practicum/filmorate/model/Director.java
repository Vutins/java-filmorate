package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor //может не понадобится
@EqualsAndHashCode(of = {"id"})
public class Director {
    public Long id;
    @NotNull(message = "Имя режиссёра обязательно к заполнению")
    @NotBlank(message = "Имя режиссёра не может быть пустым")
    public String name;
}
