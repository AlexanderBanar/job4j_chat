package ru.job4j_chat.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j_chat.model.Person;

public interface PersonRepo extends CrudRepository<Person, Integer> {
}