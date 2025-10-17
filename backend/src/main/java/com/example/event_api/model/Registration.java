package com.example.event_api.model;

import jakarta.persistence.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "REGISTRATIONS")
public class Registration {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name="CUSTOMER_ID", nullable = false)
    private Long customerId;

    @Column(name="EVENT_ID", nullable = false)
    private Long eventId;

    @Column(name="REGISTRATION_DATE", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name="STATUS", nullable = false)
    private String status;
}
