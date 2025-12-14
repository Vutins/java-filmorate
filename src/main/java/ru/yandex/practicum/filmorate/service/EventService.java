package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.repositories.EventDbStorage;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventTypes;
import ru.yandex.practicum.filmorate.model.enums.OperationTypes;

import java.util.List;

@Slf4j
@Service
public class EventService {

    private final EventDbStorage eventsDbStorage;

    public EventService(EventDbStorage eventsDbStorage) {
        this.eventsDbStorage = eventsDbStorage;
    }

    public List<Event> getFeed(Long userId) {
        log.info("Получение ленты событий user ID {}", userId);
        return List.copyOf(eventsDbStorage.getFeed(userId));
    }

    public Event addEvent(Long userId, EventTypes event, OperationTypes operation, Long entityId) {
        log.info("Добавление события {}-{} в ленту user ID {}", event, operation, userId);
        return eventsDbStorage.addEvent(userId, event, operation, entityId);
    }
}
