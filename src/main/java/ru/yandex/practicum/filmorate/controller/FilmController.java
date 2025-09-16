package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Set;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        return this.filmService.addFilm(film);
    }

    @GetMapping
    public Set<Film> getAllFilms() {
        return this.filmService.getFilms();
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return this.filmService.updateFilm(film);
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