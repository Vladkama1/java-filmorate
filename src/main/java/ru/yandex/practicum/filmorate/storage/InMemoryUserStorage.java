package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private Long id = 1L;
    private final Map<Long, User> users = new HashMap<>();


    @Override
    public User save(User user) {
        user.setId(createIdUser());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> update(User user) {
        if (!users.containsKey(user.getId())) {
            return Optional.empty();
        }
        users.put(user.getId(), user);
        return Optional.of(user);
    }

    @Override
    public boolean delete(Long id, Long friendId) {
        users.get(id).getFriends().remove(friendId);
        return users.get(friendId).getFriends().remove(id);
    }

    @Override
    public boolean isExistById(Long id) {
        return users.containsKey(id);
    }

    @Override
    public boolean addFriend(Long id, Long friendId) {
        users.get(id).getFriends().add(friendId);
        return users.get(friendId).getFriends().add(id);
    }

    @Override
    public List<User> getAllFriends(Long id) {
        List<User> listFriends = new ArrayList<>();
        for (Long friendId : users.get(id).getFriends()) {
            listFriends.add(users.get(friendId));
        }
        return listFriends;
    }

    @Override
    public List<User> getAllMutualFriends(Long id, Long otherId) {
        List<User> listFriends = new ArrayList<>();
        List<Long> userFriendsId = new ArrayList<>(users.get(id).getFriends());
        List<Long> otherFriendsId = new ArrayList<>(users.get(otherId).getFriends());
        for (Long idFr : userFriendsId) {
            if (otherFriendsId.contains(idFr)) {
                listFriends.add(users.get(idFr));
            }
        }
        return listFriends;
    }

    private Long createIdUser() {
        return id++;
    }
}
