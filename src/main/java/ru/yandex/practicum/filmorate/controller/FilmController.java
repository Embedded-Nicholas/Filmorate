package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmCreateRequest;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.FilmUpdateRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dto.mapper.FilmDtoMapper;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Set;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FilmController {
    private final FilmService filmService;
    private final FilmDtoMapper filmDtoMapper;

    @PostMapping
    public FilmDto addFilm(@RequestBody FilmCreateRequest request) {
        Film film = filmDtoMapper.fromCreate(request);
        return filmDtoMapper.toDto(this.filmService.addFilm(film));
    }

    @GetMapping
    public Set<FilmDto> getAllFilms() {
        Set<Film> films = this.filmService.getFilms();
        Set<FilmDto> dtos = new java.util.LinkedHashSet<>();
        for (Film f : films) {
            dtos.add(filmDtoMapper.toDto(f));
        }
        return dtos;
    }

    @PutMapping
    public FilmDto updateFilm(@RequestBody FilmUpdateRequest request) {
        Film film = filmDtoMapper.fromUpdate(request);
        return filmDtoMapper.toDto(this.filmService.updateFilm(film));
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable String id, @PathVariable String userId) {
        this.filmService.addLike(Long.parseLong(id), Long.parseLong(userId));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable String id, @PathVariable String userId) {
        this.filmService.removeLike(Long.parseLong(id), Long.parseLong(userId));
    }

    @GetMapping("/popular")
    public Set<Film> getPopularFilms(@RequestParam  (defaultValue = "10") String count) {
        System.out.println(count);
        return this.filmService.findTopCountByLikes(Integer.parseInt(count));
    }
}