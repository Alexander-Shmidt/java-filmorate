package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validators.Validator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Validator validator = new Validator();

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> allFilms() {
        return films.values();
    }

    @PostMapping
    public ResponseEntity addFilm(@RequestBody Film film) {
        try {
            validator.addOrUpdateFilmValidation(film);
        } catch (ValidationException e) {
            return ResponseEntity.internalServerError().body(e);
        }

        log.debug("Добавлен фильм {}", film.getName());
        films.put(film.getId(), film);
        return ResponseEntity.ok(film);
    }

    @PutMapping
    public ResponseEntity updateFilm(@RequestBody Film film) {
        try {
            validator.addOrUpdateFilmValidation(film);
        } catch (ValidationException e) {
            return ResponseEntity.internalServerError().body(e);
        }
        log.debug("Изменен фильм {}", film.getName());
        films.put(film.getId(), film);
        return ResponseEntity.ok(film);
    }

}

