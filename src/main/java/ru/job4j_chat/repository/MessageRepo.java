package ru.job4j_chat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j_chat.model.Message;

public interface MessageRepo extends CrudRepository<Message, Integer> {
}