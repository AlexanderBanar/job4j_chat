package ru.job4j_chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j_chat.model.Message;
import ru.job4j_chat.model.Person;
import ru.job4j_chat.model.Room;
import ru.job4j_chat.repository.MessageRepo;
import ru.job4j_chat.repository.PersonRepo;
import ru.job4j_chat.repository.RoomRepo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
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
    private final ObjectMapper objectMapper;

    public MessageController(final MessageRepo messages, final PersonRepo persons,
                             final RoomRepo rooms, final ObjectMapper mapper) {
        this.messages = messages;
        this.persons = persons;
        this.rooms = rooms;
        this.objectMapper = mapper;
    }

    @PostMapping("/")
    public ResponseEntity<Message> create(@RequestParam String text, @RequestParam int personId,
                                          @RequestParam int roomId) {
        if (text == null || personId == 0 || roomId == 0) {
            throw new NullPointerException("Text or personId or roomId must not be empty or 0");
        }
        if (text.contains("bad word")) {
            throw new IllegalArgumentException("Invalid text. Text must be free of rude words");
        }
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
    public Message findById(@PathVariable int id) {
        return this.messages.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Message not found, please check its id"
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@RequestParam String newText, @PathVariable int id) {
        if (newText == null || id == 0) {
            throw new NullPointerException("NewText or messageId must not be empty or 0");
        }
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

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public void exceptionHandler(Exception e, HttpServletRequest request,
                                 HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
    }
}