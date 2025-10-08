package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreItem;
import ru.yandex.practicum.filmorate.dto.MpaItem;
import ru.yandex.practicum.filmorate.storage.db.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DictionaryService {
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    public List<GenreItem> getGenres() { return genreDbStorage.findAll(); }
    public Optional<GenreItem> getGenre(Long id) { return genreDbStorage.findById(id); }

    public List<MpaItem> getMpa() { return mpaDbStorage.findAll(); }
    public Optional<MpaItem> getMpa(Long id) { return mpaDbStorage.findById(id); }
}


