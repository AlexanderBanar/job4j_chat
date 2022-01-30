package ru.job4j_chat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.job4j_chat.model.Message;
import ru.job4j_chat.model.Person;
import ru.job4j_chat.model.Room;
import ru.job4j_chat.repository.MessageRepo;
import ru.job4j_chat.repository.PersonRepo;
import ru.job4j_chat.repository.RoomRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/message")
public class MessageController {
    private final MessageRepo messages;
    private final PersonRepo persons;
    private final RoomRepo rooms;

    public MessageController(final MessageRepo messages, final PersonRepo persons,
                             final RoomRepo rooms) {
        this.messages = messages;
        this.persons = persons;
        this.rooms = rooms;
    }

    @PostMapping("/")
    public ResponseEntity<Message> create(@RequestParam String text, @RequestParam int personId,
                                          @RequestParam int roomId) {
        Message message = Message.of(text);
        Optional personOpt = persons.findById(personId);
        Optional roomOpt = rooms.findById(roomId);
        if (!personOpt.isPresent() || !roomOpt.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        message.setPerson((Person) personOpt.get());
        message.setRoom((Room) roomOpt.get());
        return new ResponseEntity<>(
                this.messages.save(message),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/")
    public List<Message> findAll() {
        return StreamSupport.stream(
                this.messages.findAll().spliterator(), false
        ).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> findById(@PathVariable int id) {
        var message = this.messages.findById(id);
        return new ResponseEntity<>(
                message.orElse(new Message()),
                message.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@RequestParam String newText, @PathVariable int id) {
        Optional messageOpt = messages.findById(id);
        if (!messageOpt.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Message message = (Message) messageOpt.get();
        message.setText(newText);
        this.messages.save(message);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Message message = new Message();
        message.setId(id);
        this.messages.delete(message);
        return ResponseEntity.ok().build();
    }
}