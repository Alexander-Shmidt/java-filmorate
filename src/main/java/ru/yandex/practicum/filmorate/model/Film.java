package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Data
public class Film {
    private int id;
    @NonNull
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final Long duration;
    private Set<Integer> like;

    public Film(int id, @NonNull String name, String description, LocalDate releaseDate, Long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.like = Set.of();
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Integer> getLike() {
        return like;
    }

    public void setLike(Set<Integer> like) {
        this.like = like;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return id == film.id && name.equals(film.name) && Objects.equals(description, film.description)
                && Objects.equals(releaseDate, film.releaseDate) && Objects.equals(duration, film.duration)
                && Objects.equals(like, film.like);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
