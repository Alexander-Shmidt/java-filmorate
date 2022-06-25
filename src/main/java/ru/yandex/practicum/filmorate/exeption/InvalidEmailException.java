package ru.yandex.practicum.filmorate.exeption;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
