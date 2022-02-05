package ru.job4j_chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j_chat.model.Person;
import ru.job4j_chat.repository.PersonRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@RequestMapping("/person")
public class PersonController {
    private final PersonRepo persons;
    private BCryptPasswordEncoder encoder;

    public PersonController(final PersonRepo persons,
                            BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
        this.persons = persons;
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestParam String name) {
        if (name == null) {
            throw new NullPointerException("Name must not be empty");
        }
        return new ResponseEntity<>(
                this.persons.save(Person.of(name)),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/sign-up")
    public void signUp(@RequestParam String name, @RequestParam String password) {
        if (name == null || password == null) {
            throw new NullPointerException("Name or password must not be empty");
        }
        Person person = Person.of(name);
        person.setPassword(encoder.encode(password));
        this.persons.save(person);
    }

    @GetMapping("/")
    public List<Person> findAll() {
        return StreamSupport.stream(
                this.persons.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Person findById(@PathVariable int id) {
        return this.persons.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person not found, please check its id"
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@RequestParam String newName, @PathVariable int id) {
        if (newName == null || id == 0) {
            throw new NullPointerException("NewName or personId must not be empty or 0");
        }
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
        if (id == 0) {
            throw new NullPointerException("PersonId must not be 0");
        }
        Person person = new Person();
        person.setId(id);
        this.persons.delete(person);
        return ResponseEntity.ok().build();
    }
}