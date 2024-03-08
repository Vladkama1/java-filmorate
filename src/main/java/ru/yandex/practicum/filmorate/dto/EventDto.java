package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

@Data
@Builder
public class EventDto {

    private Long eventId;

    private Long userId;

    private EventType eventType;

    private Operation operation;

    private Long entityId;

    private Integer timestamp;
}
