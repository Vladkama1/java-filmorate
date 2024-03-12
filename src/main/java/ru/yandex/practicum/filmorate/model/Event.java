package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.time.Instant;

@Data
@Builder(toBuilder = true)
public class Event {
    private Long id;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long entityId;
    private Instant timestamp;
}
