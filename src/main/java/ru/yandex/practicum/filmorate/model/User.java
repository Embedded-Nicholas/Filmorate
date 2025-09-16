package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
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

    private Set<Long> friends;

    private int friendsCount;

    public void addFriend(Long friendId) {
        friends.add(friendId);
        this.friendsCount++;
    }

    public void removeFriend(Long friendId) {
        friends.remove(friendId);
        this.friendsCount--;
    }

    public boolean isFriend(Long friendId) {
        return friends.contains(friendId);
    }

    public int getFriendsCount() {
        return friends!= null? this.friendsCount: 0;
    }

}