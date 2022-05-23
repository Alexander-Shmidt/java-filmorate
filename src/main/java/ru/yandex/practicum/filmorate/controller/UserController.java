package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validators.Validator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Validator validator = new Validator();
    private final Map<String, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public ResponseEntity create(@RequestBody User user) {
        try {
            validator.addOrUpdateUserValidation(user);
            if (users.containsKey(user.getEmail())) {
                throw new ValidationException("Пользователь с электронной почтой " +
                        user.getEmail() + " уже зарегистрирован.");
            }
        } catch (ValidationException e) {
            return ResponseEntity.internalServerError().body(e);
        }
        log.debug("Добавлена запись о пользователе {}", user.getName());
        users.put(user.getEmail(), user);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity put(@RequestBody User user) {
        try {
            validator.addOrUpdateUserValidation(user);
        } catch (ValidationException e) {
            return ResponseEntity.internalServerError().body(e);
        }
        log.debug("Изменена запись о пользователе {}", user.getName());
        users.put(user.getEmail(), user);
        return ResponseEntity.ok(user);
    }

    public void clearUsers() {
        users.clear();
    }
}
