package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User findUser(int id);

    Collection<User> findAll();

    void putUser(User user);
}
