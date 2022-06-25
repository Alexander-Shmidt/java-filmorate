package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.validators.Validator;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    final
    FilmService filmService;

    final
    InMemoryFilmStorage inMemoryFilmStorage;

    public FilmController(InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
    }

    private final Validator validator = new Validator();

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return inMemoryFilmStorage.getFilm(id);
    }

    @GetMapping
    public List<Film> allFilms() {
        return inMemoryFilmStorage.getAllFilms();
    }

    @PostMapping
    public ResponseEntity addFilm(@RequestBody Film film) {
        try {
            validator.addOrUpdateFilmValidation(film);
        } catch (ValidationException e) {
            return ResponseEntity.internalServerError().body(e);
        }

        log.debug("Добавлен фильм {}", film.getName());
        inMemoryFilmStorage.addFilm(film);
        return ResponseEntity.ok(film);
    }

    @PutMapping
    public ResponseEntity updateFilm(@RequestBody Film film) {
        try {
            validator.addOrUpdateFilmValidation(film);
        } catch (ValidationException e) {
            return ResponseEntity.status(404).body(e);
        }
        log.debug("Изменен фильм {}", film.getName());
        inMemoryFilmStorage.addFilm(film);
        return ResponseEntity.ok(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity getLikeToFilm(@PathVariable("id") int idFilm, @PathVariable int userId) {
        try {
            if (!inMemoryFilmStorage.getAllFilms().isEmpty() &&
                    inMemoryFilmStorage.getAllFilms().contains(inMemoryFilmStorage.getFilm(idFilm)))
                validator.likes(inMemoryFilmStorage.getFilm(idFilm), userId);
        } catch (ValidationException e) {
            return ResponseEntity.status(404).body(e);
        }
        log.debug("Добавлен лайк от пользователя с ID ", userId);
        inMemoryFilmStorage.addLikeToFilm(idFilm, userId);
        return ResponseEntity.ok(inMemoryFilmStorage.getFilm(idFilm));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity deleteLikeFromFilm(@PathVariable("id") int idFilm, @PathVariable int userId) {
        try {
            if (!inMemoryFilmStorage.getAllFilms().isEmpty() &&
                    inMemoryFilmStorage.getAllFilms().contains(inMemoryFilmStorage.getFilm(idFilm)))
                validator.deleteLikes(inMemoryFilmStorage.getFilm(idFilm), userId);
        } catch (ValidationException e) {
            return ResponseEntity.status(404).body(e);
        }
        log.debug("Удален лайк пользователя с ID ", userId);
        inMemoryFilmStorage.deleteLikeFromFilm(idFilm, userId);
        return ResponseEntity.ok(inMemoryFilmStorage.getFilm(idFilm));
    }

    @GetMapping("/popular")
    public List<Film> getTopCountFilms(@RequestParam(defaultValue = "10",required = false) Integer count) {
        if (count <= 0) throw new ValidationException("Количество фильмов не может быть отрицательным");
        log.debug("Показываем " + count + " фильмов с самым высоким рейтингом");
        return filmService.getTopCountFilms(count);
    }

}

