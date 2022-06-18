package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validators.Validator;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Validator validator = new Validator();
    final UserService userService;
    final InMemoryUserStorage inMemoryUserStorage;

    public UserController(UserService userService, InMemoryUserStorage inMemoryUserStorage) {
        this.userService = userService;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }


    @GetMapping
    public List<User> findAll() {
        return inMemoryUserStorage.findAll();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable("id") int id) {
        return inMemoryUserStorage.findUser(id);
    }

    @PostMapping
    public ResponseEntity create(@RequestBody User user) {
        try {
            validator.addOrUpdateUserValidation(user);
            if (inMemoryUserStorage.findAll().contains(user)) {
                throw new UserAlreadyExistException("Пользователь с электронной почтой " +
                        user.getEmail() + " уже зарегистрирован.");
            }
        } catch (ValidationException e) {
            return ResponseEntity.internalServerError().body(e);
        }
        log.debug("Добавлена запись о пользователе {}", user.getName());
        inMemoryUserStorage.putUser(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity put(@RequestBody User user) {
        try {
            validator.addOrUpdateUserValidation(user);
        } catch (ValidationException e) {
            return ResponseEntity.status(404).body(e);
        }
        log.debug("Изменена запись о пользователе {}", user.getName());
        inMemoryUserStorage.putUser(user);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity addFriend(@PathVariable int id, @PathVariable int friendId) {
        try {
            validator.addFriendList(inMemoryUserStorage.findUser(id), inMemoryUserStorage.findUser(friendId));

        } catch (ValidationException e) {
            return ResponseEntity.internalServerError().body(e);
        }
        userService.addFriends(id, friendId);
        return ResponseEntity.ok(userService.showUser(id));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        validator.deleteFriendList(inMemoryUserStorage.findUser(id), inMemoryUserStorage.findUser(friendId));
        userService.deleteFromFriends(id, friendId);
        return userService.showUser(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> showFriendsByAuthorId(@PathVariable int id) {
        return userService.showFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> showCommonFriends(@PathVariable("id") int idAuthor, @PathVariable("otherId") int friendId) {
        return userService.showComFriends(idAuthor, friendId);
    }


    public void clearUsers() {
        inMemoryUserStorage.clearUsers();
    }
}
