package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    private Long id;

    private Long userId;

    private EventType eventType;

    private Operation operation;

    private Long entityId;

    private Instant timestamp;

    public EventDto toDto() {
        return EventDto.builder()
                .eventId(this.id)
                .userId(this.userId)
                .eventType(this.eventType)
                .operation(this.operation)
                .entityId(this.entityId)
                .timestamp(timestamp.toEpochMilli())
                .build();
    }

    public Event(EventDto eventDto) {
        this.userId = eventDto.getUserId();
        this.eventType = eventDto.getEventType();
        this.operation = eventDto.getOperation();
        this.entityId = eventDto.getEntityId();
    }
}
