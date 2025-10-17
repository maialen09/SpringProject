package com.example.event_api.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import com.example.event_api.repository.EventRepository;
import com.example.event_api.model.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;

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
}
