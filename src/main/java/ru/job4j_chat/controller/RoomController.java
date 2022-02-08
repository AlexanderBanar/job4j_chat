package ru.job4j_chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j_chat.model.Person;
import ru.job4j_chat.model.Room;
import ru.job4j_chat.repository.PersonRepo;
import ru.job4j_chat.repository.RoomRepo;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Validated
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
    public ResponseEntity<Room> create(@NotEmpty @RequestParam String description,
                                       @Positive @RequestParam int personId) {
        if (description == null || personId == 0) {
            throw new NullPointerException("Description or personId must not be empty or 0");
        }
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
    public Room findById(@Positive @PathVariable int id) {
        return this.rooms.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Room not found, please check its id"
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@NotEmpty @RequestParam String newDescription,
                                       @Positive @PathVariable int id) {
        if (newDescription == null || id == 0) {
            throw new NullPointerException("NewDescription or roomId must not be empty or 0");
        }
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

    @PatchMapping("/{id}")
    public ResponseEntity<Void> patch(@Valid @RequestBody Room room,
                                      @Positive @PathVariable int id) {
        Room roomPatched;
        Person updatedPerson;
        Optional roomPatchedOpt = rooms.findById(id);
        if (roomPatchedOpt.isPresent()) {
            roomPatched = (Room) roomPatchedOpt.get();
        } else {
            throw new NullPointerException("RoomId is incorrect");
        }
        if (room.getPerson() != null) {
            if (room.getPerson().getId() == 0) {
                throw new NullPointerException("PersonId inside Room cannot be 0");
            } else {
                Optional updatedPersonOpt = persons.findById(room.getPerson().getId());
                if (updatedPersonOpt.isEmpty()) {
                    throw new NullPointerException("PersonId inside Room is incorrect");
                } else {
                    updatedPerson = (Person) updatedPersonOpt.get();
                    roomPatched.setPerson(updatedPerson);
                    rooms.save(roomPatched);
                }
            }
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Positive @PathVariable int id) {
        if (id == 0) {
            throw new NullPointerException("RoomId must not be 0");
        }
        Room room = new Room();
        room.setId(id);
        this.rooms.delete(room);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>(
                "not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST
        );
    }
}