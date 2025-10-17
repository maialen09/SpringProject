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
	String event_name;
	
	String event_date;

    String location;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEventName() {
		return event_name;
	}

	public void setEventName(String event_name) {
		this.event_name = event_name;
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

	public Event(long id, String event_name, String event_date, String location) {
		this.id = id;
		this.event_name = event_name;
        this.event_date = event_date;
        this.location = location;
		
	}
}
