package ru.yandex.practicum.filmorate;

import org.junit.Assert;
import org.junit.Before;
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
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * Проверяет методы UserController.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @Autowired
    private TestRestTemplate template;
    @Autowired
    private UserController userController;

    @Before
    public void init() {
        userController.clearUsers();
    }

    // Успешное добавление пользователя
    @Test
    public void addUserNewUserTestSuccess() {
        User user = new User(
                1,
                "student.practicum@yandex.ru",
                "Alex",
                LocalDate.of(1970, 6, 8)
        );
        user.setName("Alex1");
        ResponseEntity<User> response = template.postForEntity("/users", user, User.class);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(1, response.getBody().getId());
        Assert.assertEquals("student.practicum@yandex.ru", response.getBody().getEmail());
        Assert.assertEquals("Alex", response.getBody().getLogin());
        Assert.assertEquals("Alex1", response.getBody().getName());
        Assert.assertEquals(LocalDate.of(1970, 6, 8), response.getBody().getBirthday());
    }

    // Неуспешное добавление - эл почта отсутствует.
    @Test
    public void addUserNewUserEmailIsEmptyTestValidationError() {
        User user = new User(
                1,
                "",
                "Alex",
                LocalDate.of(1970, 6, 8)
        );
        user.setName("Alex1");
        HttpEntity<User> request = new HttpEntity<>(user);
        ResponseEntity response = template.postForEntity(
                "/users",
                request,
                ValidationException.class
        );
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertEquals(
                "Адрес электронной почты указан неверно.",
                ((ValidationException) response.getBody()).getMessage()
        );
    }

    // Неуспешное добавление - неверный формат эл почты.
    @Test
    public void addUserNewUserEmailTestValidationError() {
        User user = new User(
                1,
                "student.practicum.yandex.ru",
                "Alex",
                LocalDate.of(1970, 6, 8)
        );
        user.setName("Alex1");
        HttpEntity<User> request = new HttpEntity<>(user);
        ResponseEntity response = template.postForEntity(
                "/users",
                request,
                ValidationException.class
        );
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertEquals(
                "Адрес электронной почты указан неверно.",
                ((ValidationException) response.getBody()).getMessage()
        );
    }

    // Неуспешное добавление - неверный логин.
    @Test
    public void addUserNewUserBadLoginTestValidationError() {
        User user = new User(
                1,
                "student.practicum@yandex.ru",
                "Alex Shmidt",
                LocalDate.of(1970, 6, 8)
        );
        user.setName("Alex1");
        HttpEntity<User> request = new HttpEntity<>(user);
        ResponseEntity response = template.postForEntity(
                "/users",
                request,
                ValidationException.class
        );
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertEquals(
                "Логин введен не верно.",
                ((ValidationException) response.getBody()).getMessage()
        );
    }

    // Неуспешное добавление - день рождения позже текущей даты.
    @Test
    public void addUserNewUserBirthdayInFutureTestValidationError() {
        User user = new User(
                1,
                "student.practicum@yandex.ru",
                "Alex",
                LocalDate.of(2023, 6, 8)
        );
        user.setName("Alex1");
        HttpEntity<User> request = new HttpEntity<>(user);
        ResponseEntity response = template.postForEntity(
                "/users",
                request,
                ValidationException.class
        );
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertEquals(
                "Вы еще не родились!",
                ((ValidationException) response.getBody()).getMessage()
        );
    }

    // Получение списка пользователей
    @Test
    public void allUsers() {
        User user1 = new User(
                1,
                "student1.practicum@yandex.ru",
                "Alex",
                LocalDate.of(1970, 6, 8)
        );
        user1.setName("Alex1");
        User user2 = new User(
                2,
                "student2.practicum@yandex.ru",
                "Tanya",
                LocalDate.of(1985, 12, 28)
        );
        user2.setName("Tanya1");
        template.postForEntity("/users", user1, User.class);
        template.postForEntity("/users", user2, User.class);

        ResponseEntity<Collection<User>> response = template.exchange(
                "/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<User>>() {
                }
        );
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(2, response.getBody().size());
    }

    // Обновление данных пользователя.
    @Test
    public void updateUser() {
        User user1 = new User(
                1,
                "student1.practicum@yandex.ru",
                "Alex",
                LocalDate.of(1970, 6, 8)
        );
        user1.setName("Alex1");
        template.postForEntity("/users", user1, User.class);

        User user2 = new User(
                1,
                "student1.practicum@yandex.ru",
                "Alex12",
                LocalDate.of(1970, 6, 8)
        );
        user2.setName("Alex1");
        template.put("/users", user2, User.class);

        ResponseEntity<Collection<User>> response = template.exchange(
                "/users",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Collection<User>>() {
                }
        );
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertEquals(1, response.getBody().size());
        Assert.assertEquals("Alex12", ((List<User>) response.getBody()).get(0).getLogin());

    }

}
