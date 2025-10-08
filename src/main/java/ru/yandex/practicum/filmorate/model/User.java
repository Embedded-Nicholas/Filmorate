package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.enums.FriendRequestStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {
    @Positive
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный email")
    @EqualsAndHashCode.Include
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @EqualsAndHashCode.Include
    private String login;

    @EqualsAndHashCode.Include
    private String name;

    @NotNull(message = "Дата рождения не может быть пустой")
    private LocalDate birthday;

    private int friendsCount;

    private final Map<Long, FriendRequestStatus> outgoingRequests = new HashMap<>();

    private final Map<Long, FriendRequestStatus> incomingRequests = new HashMap<>();

    private final Map<Long, FriendRequestStatus> friendRequests;
}