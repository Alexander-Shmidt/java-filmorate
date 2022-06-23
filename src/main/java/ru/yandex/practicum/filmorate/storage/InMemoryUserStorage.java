package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static ru.yandex.practicum.filmorate.GlobalConstant.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, HashSet<Integer>> friendList = new HashMap<>();

    @Override
    public User findUser(int id) {
        if (users.isEmpty() || !users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        return users.values().stream().collect(Collectors.toList());
    }

    @Override
    public void putUser(User user) {
        if (users.containsKey(user.getId())) users.replace(user.getId(), user);
        else {
            user.setId(++GLOBAL_USER_ID);
            users.put(user.getId(), user);
        }
    }

    public User findIdUser(int id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователя с таким ID не существует");
        }
        return users.getOrDefault(id, null);
    }

    public void addFriend(Integer id, Integer friendId) {
        if (friendList.isEmpty() || !friendList.containsKey(id)) friendList.put(id, new HashSet<>());
        friendList.get(id).add(friendId);
        findUser(id).setFriends(friendList.get(id));
        if (!friendList.containsKey(friendId)) friendList.put(friendId, new HashSet<>());
        friendList.get(friendId).add(id);
        findUser(friendId).setFriends(friendList.get(friendId));
    }

    public void delFriend(Integer idAuthor, Integer idFriend) {
        if (friendList.isEmpty() || !friendList.containsKey(idAuthor) || !friendList.containsKey(idFriend))
            throw new UserNotFoundException("Пользователя (или друга) с таким ID не найдено");
        friendList.get(idAuthor).remove(idFriend);
        friendList.get(idFriend).remove(idAuthor);
        findUser(idAuthor).setFriends(friendList.get(idAuthor));
        findUser(idFriend).setFriends(friendList.get(idFriend));
    }

    public void clearUsers() {
        users.clear();
        friendList.clear();
        GLOBAL_USER_ID = 0;
        GLOBAL_FILM_ID = 0;
    }
}
