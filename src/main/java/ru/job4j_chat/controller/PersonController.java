package ru.job4j_chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.job4j_chat.model.Person;
import ru.job4j_chat.repository.PersonRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonRepo persons;

    public PersonController(final PersonRepo persons) {
        this.persons = persons;
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestParam String name) {
        return new ResponseEntity<>(
                this.persons.save(Person.of(name)),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/")
    public List<Person> findAll() {
        return StreamSupport.stream(
                this.persons.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = this.persons.findById(id);
        return new ResponseEntity<>(
                person.orElse(new Person()),
                person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@RequestParam String newName, @PathVariable int id) {
        Optional personOpt = persons.findById(id);
        if (personOpt.isPresent()) {
            Person person = (Person) personOpt.get();
            person.setName(newName);
            this.persons.save(person);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        this.persons.delete(person);
        return ResponseEntity.ok().build();
    }
}