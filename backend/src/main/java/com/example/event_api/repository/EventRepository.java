package com.example.event_api.repository;

import org.springframework.data.repository.CrudRepository;
import com.example.event_api.model.Event;

public interface EventRepository extends CrudRepository<Event, Long>{
    
}
