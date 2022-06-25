package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final InMemoryFilmStorage filmsStorage;

    public FilmService(InMemoryFilmStorage filmsStorage) {
        this.filmsStorage = filmsStorage;
    }

    Set<Film> topFilms = new TreeSet<>(this::compare); // Упорядоченный список по количеству лайков

    public List<Film> getTopCountFilms(Integer count) {
        topFilms.addAll(filmsStorage.getAllFilms());
        return topFilms.stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film p0, Film p1) {
        return Integer.compare(p1.getLike().size(), p0.getLike().size());
    }
}
