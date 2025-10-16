package com.example.event_api.controller;

import com.example.event_api.model.Customer;
import com.example.event_api.repository.CustomerRepository;
import com.example.event_api.account.LoginRequest;
import com.example.event_api.account.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AccountController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // Root endpoint: GET /account
    @GetMapping
    public String accountStatus() {
        return "Account service is up and running!";
    }

    // Token endpoint: POST /account/token
    @PostMapping("/token")
    public ResponseEntity<?> getToken(@RequestBody LoginRequest loginRequest) {
        Customer customer = customerRepository.findByName(loginRequest.getUsername());
        if (customer == null || !customer.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
        String token = jwtUtil.generateToken(customer.getName());
        return ResponseEntity.ok(token);
    }
}
