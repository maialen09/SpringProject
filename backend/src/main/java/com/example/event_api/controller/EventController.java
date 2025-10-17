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
	public EventRepository eventRepository;

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
    
//    @GetMapping("/{id}")
//     public ResponseEntity<Event> getEventById(@PathVariable Long id) {
//         return eventRepository.findById(id)
//                 .map(event -> new ResponseEntity<>(event, HttpStatus.OK))
//                 .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//     }  
    
//     //create
//     @PostMapping()
//     public ResponseEntity<List<Event>> createEvents(@RequestBody Event event) {
//         eventRepository.save(event);
//         List<Event> events = (List<Event>) eventRepository.findAll();
//         System.out.println("Created event with id: " + event.getId());
//         return new ResponseEntity<>(events, HttpStatus.CREATED);    
//     }

//     //update
//     @PutMapping("/{id}")
//     public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event) {
//         Event existingEvent = eventRepository.findById(id).orElse(null);
//         if (existingEvent == null) {
//             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//         }
//         existingEvent.setEventName(event.getEventName());
//         existingEvent.setEventDate(event.getEventDate());
//         existingEvent.setLocation(event.getLocation());
//         eventRepository.save(existingEvent);
//         System.out.println("Updated event with id: " + id);
//         return new ResponseEntity<>(existingEvent, HttpStatus.OK);
//     }

//     //delete
//     @DeleteMapping("/{id}")
//     public ResponseEntity<List<Event>> deleteEvent(@PathVariable Long id) {
//         if (!eventRepository.existsById(id)) {
//             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//         }
//         eventRepository.deleteById(id);
//         List<Event> events = (List<Event>) eventRepository.findAll();
//         System.out.println("Deleted event with id: " + id);
//         return new ResponseEntity<>(events, HttpStatus.OK);
//     }
}

