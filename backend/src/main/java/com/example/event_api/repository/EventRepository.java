package com.example.event_api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.event_api.model.Event;

@Repository
public interface EventRepository extends CrudRepository<Event, Long>{
    
}
