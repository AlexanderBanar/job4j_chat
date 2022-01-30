package ru.job4j_chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.job4j_chat.model.Person;
import ru.job4j_chat.model.Room;
import ru.job4j_chat.repository.PersonRepo;
import ru.job4j_chat.repository.RoomRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/room")
public class RoomController {
    private final RoomRepo rooms;
    private final PersonRepo persons;

    public RoomController(final RoomRepo rooms, final PersonRepo persons) {
        this.rooms = rooms;
        this.persons = persons;
    }

    @PostMapping("/")
    public ResponseEntity<Room> create(@RequestParam String description, @RequestParam int personId) {
        Room room = Room.of(description);
        Optional personOpt = persons.findById(personId);
        if (personOpt.isPresent()) {
            Person person = (Person) personOpt.get();
            room.setPerson(person);
            return new ResponseEntity<>(
                    this.rooms.save(room),
                    HttpStatus.CREATED
            );
        } else {
            return new ResponseEntity<>(
                    null,
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @GetMapping("/")
    public List<Room> findAll() {
        return StreamSupport.stream(
                this.rooms.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> findById(@PathVariable int id) {
        var room = this.rooms.findById(id);
        return new ResponseEntity<>(
                room.orElse(new Room()),
                room.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@RequestParam String newDescription, @PathVariable int id) {
        Optional roomOpt = rooms.findById(id);
        if (roomOpt.isPresent()) {
            Room room = (Room) roomOpt.get();
            room.setDescription(newDescription);
            this.rooms.save(room);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Room room = new Room();
        room.setId(id);
        this.rooms.delete(room);
        return ResponseEntity.ok().build();
    }
}