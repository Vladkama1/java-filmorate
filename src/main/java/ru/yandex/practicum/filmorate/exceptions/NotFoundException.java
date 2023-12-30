package ru.yandex.practicum.filmorate.exceptions;

import lombok.*;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class NotFoundException extends RuntimeException {
    private final String message;
    private final HttpStatus httpStatus;
}
