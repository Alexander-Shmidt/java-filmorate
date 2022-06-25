package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class Validator {
    public void addOrUpdateFilmValidation(Film film) {
            if (film.getId() < 0) {
                throw new ValidationException("Укажите правильный идентификатор фильма.");
            }
            if (film.getName().isBlank()) {
                throw new ValidationException("Название не может быть пустым.");
            }
            if (film.getDescription().length() > 200 || film.getDescription().isBlank()) {
                throw new ValidationException("Описание занимает более 200 символов.");
            }
            if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ValidationException("Неверная дата релиза.");
            }
            if (film.getDuration() < 0) {
                throw new ValidationException("Продолжительность фильма не может быть отрицательной.");
            }
    }

    public void addOrUpdateUserValidation(User user) {
        if (user.getId() < 0) {
            throw new ValidationException("ID пользователя не может быть отрицательным");
        }

        if(user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Адрес электронной почты указан неверно.");
        }
        if(user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин введен не верно.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Вы еще не родились!");
        }
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public void addFriendList(User author, User friend) {
        if (author.equals(friend)) throw new ValidationException("Самому себе ты всегда друг");
        else if (author.getFriends().contains(friend.getId())) {
            throw new ValidationException("Друг с ID " + friend.getId() + " уже есть в списке");
        }
    }

    public void deleteFriendList(User author, User friend) {
        if (author.equals(friend)) throw new ValidationException("Нельзя удалить самого себя из своих друзей!");
    }

    public void likes(Film film, int userId) {
        if (film.getLike().contains(userId)) throw new ValidationException("Пользователь с ID " + userId + " Уже ставил " +
                "лайк этому фильму (" + film.getName() +")");
    }

    public void deleteLikes(Film film, int userId) {
        if (!film.getLike().contains(userId)) throw new ValidationException("Пользователь с ID " + userId + " не ставил " +
                "лайк этому фильму (" + film.getName() +")");
    }
}
