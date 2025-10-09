package ru.yandex.practicum.filmorate.enums;

import java.util.Arrays;

public enum MpaRating {
    G("G", "Нет возрастных ограничений"),
    PG("PG", "Детям рекомендуется смотреть с родителями"),
    PG_13("PG-13", "Детям до 13 лет просмотр не желателен"),
    R("R", "До 17 лет только в присутствии взрослого"),
    NC_17("NC-17", "До 18 лет просмотр запрещён");

    private final String code;
    private final String description;

    MpaRating(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static MpaRating getByName(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        String normalized = name.trim().toLowerCase();

        return Arrays.stream(values())
                .filter(r -> r.code.equalsIgnoreCase(normalized)
                        || r.description.toLowerCase().equals(normalized))
                .findFirst()
                .orElse(null);
    }
}
