package com.example.event_api.controller;

import com.example.event_api.account.LoginRequest;
import com.example.event_api.account.JwtUtil;
import com.example.event_api.customer.Customer;
import com.example.event_api.customer.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AuthController {

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestBody LoginRequest loginRequest) {
        Customer customer = customerRepository.findByUsername(loginRequest.getUsername());
        if (customer == null || !customer.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
        String token = JwtUtil.generateToken(customer.getUsername());
        return ResponseEntity.ok(token);
    }
}