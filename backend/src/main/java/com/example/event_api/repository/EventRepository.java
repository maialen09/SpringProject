package com.example.event_api.repository;

import org.springframework.data.repository.*;
import com.example.event_api.model.Event;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends CrudRepository<Event, Long>{ 
    
}
