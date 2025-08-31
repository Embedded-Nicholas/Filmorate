package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorTest {

    @Test
    void validate_validUser_shouldNotThrow() {
        User user = User.builder()
                .email("test@example.com")
                .login("login")
                .name("User Name")
                .birthday(LocalDate.of(2020, 1, 1))
                .build();

        assertDoesNotThrow(() -> UserValidator.validate(user));
    }

    @Test
    void validate_invalidEmail_shouldThrow() {
        User user = User.builder()
                .email("invalid-email")
                .login("login")
                .name("User")
                .birthday(LocalDate.of(2020, 1, 1))
                .build();

        ValidationException ex = assertThrows(ValidationException.class,
                () -> UserValidator.validate(user));
        assertEquals("Некорректный email", ex.getMessage());
    }

    @Test
    void validate_emptyLogin_shouldThrow() {
        User user = User.builder()
                .email("test@example.com")
                .login("")
                .name("User")
                .birthday(LocalDate.of(2020, 1, 1))
                .build();

        ValidationException ex = assertThrows(ValidationException.class,
                () -> UserValidator.validate(user));
        assertEquals("Логин не может быть пустым", ex.getMessage());
    }

    @Test
    void validate_futureBirthday_shouldThrow() {
        User user = User.builder()
                .email("test@example.com")
                .login("login")
                .name("User")
                .birthday(LocalDate.of(2020, 1, 1).plusDays(5000))
                .build();

        ValidationException ex = assertThrows(ValidationException.class,
                () -> UserValidator.validate(user));
        assertEquals("Дата рождения не может быть в будущем", ex.getMessage());
    }

    @Test
    void validate_blankName_shouldSetLoginAsName() {
        User user = User.builder()
                .email("test@example.com")
                .login("login")
                .name("")
                .birthday(LocalDate.of(2020, 1, 1))
                .build();

        assertDoesNotThrow(() -> UserValidator.validate(user));
        assertEquals("login", user.getName());
    }
}