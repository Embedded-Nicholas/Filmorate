package ru.yandex.practicum.filmorate.enums;

import java.util.Arrays;

public enum Genre {
    COMEDY("Комедия"),
    DRAMA("Драма"),
    CARTOON("Мультфильм"),
    THRILLER("Триллер"),
    DOCUMENTARY("Документальный"),
    ACTION("Боевик");

    private final String displayName;

    Genre(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Genre getGenreByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        String normalized = name.trim().toLowerCase();

        return Arrays.stream(values())
                .filter(g -> g.name().equalsIgnoreCase(normalized)
                        || g.displayName.toLowerCase().equals(normalized))
                .findFirst()
                .orElse(null);
    }
}
