package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {
    private Long eventId; //primary key
    private Long timestamp; //время события, юнит эпоха
    private Long userId; // чья лента
    private String eventType; // одно из значений LIKE, REVIEW или FRIEND
    private String operation; // одно из значений REMOVE, ADD, UPDATE
    private Long entityId; // идентификатор сущности, с которой произошло событие
}
