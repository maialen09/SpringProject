package com.example.event_api.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import com.example.event_api.repository.CustomerRepository;
import com.example.event_api.repository.EventRepository;
import com.example.event_api.repository.RegistrationRepository;
import com.example.event_api.model.Registration;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class RegistrationController {
    
    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private EventRepository eventRepository;

    @PostMapping
    public ResponseEntity<?> createRegistration(@RequestBody Registration registration) {
        Long customerId = registration.getCustomerId();
        Long eventId = registration.getEventId();

        if (!customerRepository.existsById(customerId)) {
            return new ResponseEntity<>("Customer not found", HttpStatus.NOT_FOUND);
        }

        if (!eventRepository.existsById(eventId)) {
            return new ResponseEntity<>("Event not found", HttpStatus.NOT_FOUND);
        }

        registration.setRegistrationDate(LocalDateTime.now());
        if (registration.getStatus() == null || registration.getStatus().isEmpty()) {
            registration.setStatus("CONFIRMED");
        }

        try {
            Registration savedRegistration = registrationRepository.save(registration);
            return new ResponseEntity<>(savedRegistration, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Registration already exists or invalid data", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Registration>> getAllRegistrations() {
        List<Registration> registrations = (List<Registration>) registrationRepository.findAll();
        return new ResponseEntity<>(registrations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRegistrationById(@PathVariable Long id) {
        return registrationRepository.findById(id)
            .<ResponseEntity<?>>map(registration -> new ResponseEntity<>(registration, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>("Registration not found", HttpStatus.NOT_FOUND));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Registration>> getRegistrationsByCustomer(@PathVariable Long customerId) {
        List<Registration> registrations = registrationRepository.findByCustomerId(customerId);
        return new ResponseEntity<>(registrations, HttpStatus.OK);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Registration>> getRegistrationsByEvent(@PathVariable Long eventId) {
        List<Registration> registrations = registrationRepository.findByEventId(eventId);
        return new ResponseEntity<>(registrations, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRegistration(@PathVariable Long id, @RequestBody Registration registrationDetails) {
        return registrationRepository.findById(id)
            .<ResponseEntity<?>>map(registration -> {
                if (registrationDetails.getStatus() != null && !registrationDetails.getStatus().isEmpty()) {
                    registration.setStatus(registrationDetails.getStatus());
                }
                
                Registration updated = registrationRepository.save(registration);
                return new ResponseEntity<>(updated, HttpStatus.OK);
            })
            .orElseGet(() -> new ResponseEntity<>("Registration not found", HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRegistration(@PathVariable Long id) {
        return registrationRepository.findById(id)
            .<ResponseEntity<?>>map(registration -> {
                registrationRepository.delete(registration);
                return new ResponseEntity<>("Registration deleted successfully", HttpStatus.OK);
            })
            .orElseGet(() -> new ResponseEntity<>("Registration not found", HttpStatus.NOT_FOUND));
    }
}
