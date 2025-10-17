package com.example.event_api.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.example.event_api.model.Registration;

public interface RegistrationRepository extends CrudRepository<Registration, Long> {
    List<Registration> findByCustomerId(Long customerId);
    List<Registration> findByEventId(Long eventId);
    
}
