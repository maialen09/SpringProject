package com.example.event_api.repository;

import org.springframework.data.repository.*;
import com.example.event_api.model.Event;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface EventRepository extends CrudRepository<Event, Long>{
    Optional<Event> findByEventName(String event_name);
    boolean existsByEventName(String event_name); 
    Event findByLocation(String location);   
    
}
