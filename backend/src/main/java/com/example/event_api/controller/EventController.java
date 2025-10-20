package com.example.event_api.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import com.example.event_api.repository.EventRepository;
import com.example.event_api.model.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class EventController {
    @Autowired
	private EventRepository eventRepository;

    //check if user is admin
    private boolean isAdmin(HttpServletRequest request) {
        Boolean isAdmin = (Boolean) request.getAttribute("isAdmin");
        return isAdmin != null && isAdmin;
    }

    //Get all events
    @GetMapping()
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = (List<Event>) eventRepository.findAll();
        if (events.isEmpty()){
            System.out.println("no events found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        System.out.println("Fetched all events, count: " + events.size());
        return new ResponseEntity<>(events, HttpStatus.OK);
    }
    
   @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventRepository.findById(id)
                .map(event -> new ResponseEntity<>(event, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }  
    
    //create
    @PostMapping()
    public ResponseEntity<?> createEvents(@RequestBody Event event, HttpServletRequest request) {
        Boolean isAdminAttr = (Boolean) request.getAttribute("isAdmin");
        System.out.println("DEBUG: isAdmin attribute = " + isAdminAttr);
        
        if (!isAdmin(request)) {
            System.out.println("DEBUG: Access denied - not admin");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required to create events");
        }
        eventRepository.save(event);
        List<Event> events = (List<Event>) eventRepository.findAll();
        System.out.println("Created event with id: " + event.getId());
        return new ResponseEntity<>(events, HttpStatus.CREATED);    
    }

    //update
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody Event event, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required to update events");
        }
        Event existingEvent = eventRepository.findById(id).orElse(null);
        if (existingEvent == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        existingEvent.setEventName(event.getEventName());
        existingEvent.setEventDate(event.getEventDate());
        existingEvent.setLocation(event.getLocation());
        eventRepository.save(existingEvent);
        System.out.println("Updated event with id: " + id);
        return new ResponseEntity<>(existingEvent, HttpStatus.OK);
    }

    //delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required to delete events");
        }
        if (!eventRepository.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        eventRepository.deleteById(id);
        List<Event> events = (List<Event>) eventRepository.findAll();
        System.out.println("Deleted event with id: " + id);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }
}
