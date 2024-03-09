package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<Error> handleValidException(final ValidException e) {
        log.error("Exception ValidException: {}, статус ответа: {}", e.getMessage(), e.getHttpStatus());
        return new ResponseEntity<>(new Error("Ошибка: " + e.getMessage()), e.getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<Error> handleNotFoundException(final NotFoundException e) {
        log.error("Exception NotFoundException: {}, статус ответа: {}", e.getMessage(), e.getHttpStatus());
        return new ResponseEntity<>(new Error("Ошибка: " + e.getMessage()), e.getHttpStatus());
    }

    @ExceptionHandler
    public ResponseEntity<Error> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("Exception MethodArgumentNotValidException: {}, статус ответа: {}", e.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new Error("Ошибка: " + e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Error> handleConstraintViolationException(final ConstraintViolationException e) {
        log.error("Exception ConstraintViolationException: {}, статус ответа: {}", e.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new Error("Ошибка: " + e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Error> handleAlreadyExistsException(final AlreadyExistsException e) {
        log.error("Exception AlreadyExistsException: {}, статус ответа: {}", e.getMessage(), HttpStatus.CONFLICT);
        return new ResponseEntity<>(new Error("Ошибка: " + e.getMessage()), HttpStatus.CONFLICT);
    }
}
