package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {


    final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Autowired
    InMemoryUserStorage inMemoryUserStorage;

    public void addFriends(int authorId, int friendId) {
        if (inMemoryUserStorage.findUser(authorId).getFriends().contains(friendId)) {
            throw new UserAlreadyExistException("Этот пользователь уже в друзьях");
        }
        inMemoryUserStorage.addFriend(authorId, friendId);
    }

    public void deleteFromFriends(int authorId, int friendId) {
        // inMemoryUserStorage.findUser(authorId).getFriends().remove(friendId);
        inMemoryUserStorage.delFriend(authorId, friendId);

    }

    public User showUser(int id) {
        return inMemoryUserStorage.findIdUser(id);
    }

    public List<User> showFriends(int id) {
        List<User> users = new ArrayList<>();
        for (Integer fr : inMemoryUserStorage.findUser(id).getFriends()) {
            users.add(inMemoryUserStorage.findUser(fr));
        }
        return users;
    }

    public List<User> showComFriends(int idAuthor, int friendId) {
        List<User> user = new ArrayList<>();
        for (User user1 : showFriends(idAuthor)) {
            for (User user2 : showFriends(friendId)) {
                if (user1.equals(user2)) {
                    user.add(user1);
                    break;
                }
            }
        }
        return user;
    }
}
