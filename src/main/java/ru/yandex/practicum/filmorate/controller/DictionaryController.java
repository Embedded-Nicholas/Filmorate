package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.GenreItem;
import ru.yandex.practicum.filmorate.dto.MpaItem;
import ru.yandex.practicum.filmorate.service.DictionaryService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class DictionaryController {
    private final DictionaryService dictionaryService;

    @GetMapping("/genres")
    public List<GenreItem> getGenres() { return dictionaryService.getGenres(); }

    @GetMapping("/genres/{id}")
    public GenreItem getGenre(@PathVariable Long id) { return dictionaryService.getGenre(id).orElse(null); }

    @GetMapping("/mpa")
    public List<MpaItem> getMpa() { return dictionaryService.getMpa(); }

    @GetMapping("/mpa/{id}")
    public MpaItem getMpa(@PathVariable Long id) { return dictionaryService.getMpa(id).orElse(null); }
}


