package com.example.event_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="EVENTS")
public class Event {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;
	
	@Column(name="EVENT_NAME")
	String eventName;
	
	String event_date;

    String location;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventDate() {
		return event_date;
	}

	public void setEventDate(String event_date) {
		this.event_date = event_date;
	}
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;

    }

	public Event(){  
	}

	public Event(long id, String eventName, String event_date, String location) {
		this.id = id;
		this.eventName = eventName;
        this.event_date = event_date;
        this.location = location;
		
	}
}
