package ru.yandex.practicum.filmorate;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Проверяет методы FilmController.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmControllerTest {

    @Autowired
    private TestRestTemplate template;

    // Успешное добавление фильма
    @Test
    public void addFilmNewFilmTestSuccess() {
        Film film = new Film(
                1,
                "Матрица",
                "фэнтэзи",
                LocalDate.of(2000, 01, 01),
                (long) 2.0
        );

        ResponseEntity<Film> response = template.postForEntity("/films", film, Film.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(1, response.getBody().getId());
        Assert.assertEquals("Матрица", response.getBody().getName());
        Assert.assertEquals("фэнтэзи", response.getBody().getDescription());
        Assert.assertEquals(LocalDate.of(2000, 01, 01), response.getBody().getReleaseDate());
        Assert.assertEquals(Optional.of(2.0), response.getBody().getDuration());
    }

    // Неуспешное добавление - неправильный идентификатор фильма.
    @Test
    public void addFilmNewFilmTestValidationError() {
        Film film = new Film(
                -1,
                "Матрица",
                "фэнтэзи",
                LocalDate.of(2000, 01, 01),
                (long) 120.0
        );

        HttpEntity<Film> request = new HttpEntity<>(film);
        ResponseEntity response = template.postForEntity(
                "/films",
                request,
                ValidationException.class
        );
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertEquals(
                "Укажите правильный идентификатор фильма.",
                ((ValidationException) response.getBody()).getMessage()
        );
    }

    // Неуспешное добавление - отсутствует название фильма.
    @Test
    public void addFilmNewFilmEmptyNameTestValidationError() {
        Film film = new Film(
                1,
                "",
                "фэнтэзи",
                LocalDate.of(2000, 01, 01),
                (long) 120.0
        );

        HttpEntity<Film> request = new HttpEntity<>(film);
        ResponseEntity response = template.postForEntity(
                "/films",
                request,
                ValidationException.class
        );
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertEquals(
                "Название не может быть пустым.",
                ((ValidationException) response.getBody()).getMessage()
        );
    }

    // Неуспешное добавление - описание фильма длинее 200 символов.
    @Test
    public void addFilmNewFilmTooLongDescriptionTestValidationError() {
        Film film = new Film(
                1,
                "Матрица",
                "фэнтэзи с ужасающе длинной преамбулой и такими известными актерами, что их не счесть." +
                        " И вообще, не фильм, а сказка и бла-бла-бла. уже рука писать устала, а длина все никак не " +
                        "хочет набираться. Ну вот теперь-то точно больше 200 символов",
                LocalDate.of(2000, 01, 01),
                (long) 120.0
        );

        HttpEntity<Film> request = new HttpEntity<>(film);
        ResponseEntity response = template.postForEntity(
                "/films",
                request,
                ValidationException.class
        );
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertEquals(
                "Описание занимает более 200 символов.",
                ((ValidationException) response.getBody()).getMessage()
        );
    }

    // Неуспешное добавление - неверная дата релиза.
    @Test
    public void addFilmNewDateOfReliseTooOldTestValidationError() {
        Film film = new Film(
                1,
                "Матрица",
                "фэнтэзи",
                LocalDate.of(1893, 01, 01),
                (long) 120.0
        );

        HttpEntity<Film> request = new HttpEntity<>(film);
        ResponseEntity response = template.postForEntity(
                "/films",
                request,
                ValidationException.class
        );
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertEquals(
                "Неверная дата релиза.",
                ((ValidationException) response.getBody()).getMessage()
        );
    }

    // Неуспешное добавление - отрицательная продолжительность фильма.
    @Test
    public void addFilmNewNegativeDurationTestValidationError() {
        Film film = new Film(
                1,
                "Матрица",
                "фэнтэзи",
                LocalDate.of(2000, 01, 01),
                (long) -120.0
        );

        HttpEntity<Film> request = new HttpEntity<>(film);
        ResponseEntity response = template.postForEntity(
                "/films",
                request,
                ValidationException.class
        );
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertEquals(
                "Продолжительность фильма не может быть отрицательной.",
                ((ValidationException) response.getBody()).getMessage()
        );
    }

    // Получение списка фильмов
    @Test
    public void allFilms() {
        Film film1 = new Film(
                1,
                "Матрица",
                "фэнтэзи",
                LocalDate.of(2000, 01, 01),
                (long) 120.0
        );
        Film film2 = new Film(
                2,
                "Властелин Колец",
                "фэнтэзи",
                LocalDate.of(2001, 01, 01),
                (long) 180.0
        );
        template.postForEntity("/films", film1, Film.class);
        template.postForEntity("/films", film2, Film.class);

        ResponseEntity<Collection<Film>> response = template.exchange(
                "/films",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Film>>() {
                }
        );
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(2, response.getBody().size());
    }

    // Обновление фильма
    @Test
    public void updateFilm() {
        Film film1 = new Film(
                1,
                "Матрица",
                "фэнтэзи",
                LocalDate.of(2000, 01, 01),
                (long) 120.0
        );
        template.postForEntity("/films", film1, Film.class);

        Film film2 = new Film(
                1,
                "Матрица2",
                "фэнтэзи",
                LocalDate.of(2001, 01, 01),
                (long) 180.0
        );
        template.put("/films", film2, Film.class);

        ResponseEntity<Collection<Film>> response = template.exchange(
                "/films",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<Film>>() {
                }
        );
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(1, response.getBody().size());
        Assert.assertEquals("Матрица2", ((List<Film>) response.getBody()).get(0).getName());

    }

}
