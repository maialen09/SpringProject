package com.example.event_api.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import com.example.event_api.repository.EventRepository;
import com.example.event_api.model.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class EventController {
    @Autowired
	private EventRepository EventRepository;

    //Get all events
    @GetMapping()
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = (List<Event>) EventRepository.findAll();
        if (events.isEmpty()){
            System.out.println("no events found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        System.out.println("Fetched all events, count: " + events.size());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }
        // Get event by ID
        @GetMapping("/{id}")
        public ResponseEntity<Event> getEventById(@PathVariable Long id) {
            java.util.Optional<Event> eventOpt = EventRepository.findById(id);
            return eventOpt.map(event -> new ResponseEntity<>(event, HttpStatus.OK))
                           .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
}
