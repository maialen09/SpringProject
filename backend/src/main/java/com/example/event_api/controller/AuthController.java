package com.example.event_api.controller;

import com.example.event_api.account.LoginRequest;
import com.example.event_api.account.TokenService;
import com.example.event_api.model.Customer;
import com.example.event_api.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/account")
public class AuthController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Customer customer) {
        if (customer.getName() == null || customer.getEmail() == null || customer.getPassword() == null) {
            return ResponseEntity.badRequest().body("Name, email, and password are required");
        }
        if (customerRepository.existsByEmail(customer.getEmail())) {
            return ResponseEntity.status(409).body("Email already registered");
        }
        customerRepository.save(customer);
        return ResponseEntity.ok("Registration successful");
    }
}