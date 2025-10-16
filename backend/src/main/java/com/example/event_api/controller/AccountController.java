package com.example.event_api.controller;
import com.example.event_api.model.Customer;
import com.example.event_api.repository.CustomerRepository;
import com.example.event_api.account.LoginRequest;
import com.example.event_api.account.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/account")
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


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Customer customer) {
        // Forward registration to customer backend
        try {
            java.net.URL url = new java.net.URL("http://localhost:8080/api/customers");
            java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            String json = String.format("{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}",
                    customer.getName(), customer.getEmail(), customer.getPassword());
            try (java.io.OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            int code = con.getResponseCode();
            if (code == 409) {
                return ResponseEntity.status(409).body("Email already registered");
            }
            if (code >= 200 && code < 300) {
                return ResponseEntity.ok("Registration successful");
            } else {
                return ResponseEntity.status(code).body("Registration failed");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Registration error: " + e.getMessage());
        }
    }
}
