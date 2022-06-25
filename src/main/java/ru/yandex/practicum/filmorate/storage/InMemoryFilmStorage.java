package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import static ru.yandex.practicum.filmorate.GlobalConstant.*;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage{

    private final Map<Integer, Film> films = new HashMap<>();
    private final Map<Integer, HashSet<Integer>> listLikesByFilm = new HashMap<>();
    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }


    @Override
    public Film getFilm(int id) {
        if(films.isEmpty() || !films.containsKey(id) || id < 0) {
            throw new FilmNotFoundException("Фильм с таким идентификатором не найден!");
        } else {
            return films.get(id);
        }
    }

    @Override
    public void addFilm(Film film) {
        if (films.containsKey(film.getId())) films.replace(film.getId(), film);
        else {
            film.setId(++GLOBAL_FILM_ID);
            films.put(film.getId(), film);
        }

    }

    public void addLikeToFilm(Integer idFilm, Integer idUser) {
        if (listLikesByFilm.isEmpty() || !listLikesByFilm.containsKey(idFilm)) listLikesByFilm.put(idFilm, new HashSet<>());
        listLikesByFilm.get(idFilm).add(idUser);
        getFilm(idFilm).setLike(listLikesByFilm.get(idFilm));
    }

    public void deleteLikeFromFilm(Integer idFilm, Integer idUser) {
        listLikesByFilm.get(idFilm).remove(idUser);
        getFilm(idFilm).setLike(listLikesByFilm.get(idFilm));
    }
}
