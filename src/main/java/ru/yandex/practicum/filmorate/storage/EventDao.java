package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventDao {

    List<Event> getFriendsFeed(Long id);

    Event save(Event event);

}
