package ru.job4j_chat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j_chat.model.Room;

public interface RoomRepo extends CrudRepository<Room, Integer> {
}