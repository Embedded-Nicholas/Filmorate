package ru.yandex.practicum.filmorate.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.IncorrectCountException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({IncorrectCountException.class, ValidationException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleIncorrectCountException(final RuntimeException ex) {
        log.error("Ошибка: {}", ex.getMessage(), ex);
        Map<String, String> body = Map.of("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNonExistingUserException(ObjectNotFoundException ex) {
        log.error("Ошибка: {}", ex.getMessage(), ex);
        Map<String, String> body = Map.of("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}

