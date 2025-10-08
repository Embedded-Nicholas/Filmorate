package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.enums.Genre;
import ru.yandex.practicum.filmorate.enums.MpaRating;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Film {
    @Positive
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не может быть пустой")
    private LocalDate releaseDate;

    @NotNull
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Long duration;

    private Set<Long> likedUserIds;

    private Set<Genre> filmGenres;

    private MpaRating mpaRating;

    public void addLike(Long userId) {
        likedUserIds.add(userId);
    }

    public void removeLike(Long userId) {
        likedUserIds.remove(userId);
    }

    public int getLikesCount() {
        return likedUserIds.size();
    }
}