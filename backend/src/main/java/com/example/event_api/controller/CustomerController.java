package com.example.event_api.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import com.example.event_api.repository.CustomerRepository;
import com.example.event_api.model.Customer;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CustomerController {
    @Autowired
	private CustomerRepository customerRepository;

    //Get all customers
    @GetMapping()
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = (List<Customer>) customerRepository.findAll();
        if (customers.isEmpty()){
            System.out.println("no customers found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        System.out.println("Fetched all customers, count: " + customers.size());
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    // Get customer by username (for login/customerId lookup)
    @GetMapping("/byname")
    public ResponseEntity<Customer> getCustomerByName(@RequestParam String username) {
        java.util.Optional<Customer> customerOpt = customerRepository.findByName(username);
        if (customerOpt.isPresent()) {
            System.out.println("Fetched customer by username: " + username);
            return new ResponseEntity<>(customerOpt.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //get single customer by id
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerbyId(@PathVariable Long id){
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        System.out.println("Fetched Customer with id: " + id);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    //create
    @PostMapping()
    public ResponseEntity<List<Customer>> createCustomers(@RequestBody Customer customer) {
        customerRepository.save(customer);
        List<Customer> customers = (List<Customer>) customerRepository.findAll();
        System.out.println("Created customer with id: " + customer.getId());
        return new ResponseEntity<>(customers, HttpStatus.CREATED);
    }

    //update
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customerDetails) {
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        customer.setName(customerDetails.getName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPassword(customerDetails.getPassword());
        customerRepository.save(customer);
        System.out.println("Updated customer with id: " + id);
        return new ResponseEntity<>(customer, HttpStatus.OK);
    }

    //delete customer
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id, HttpServletRequest request) {
        // Get the logged-in user's email from the JWT
        String userEmail = (String) request.getAttribute("userEmail");
        
        Customer customer = customerRepository.findById(id).orElse(null);
        if (customer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        // Prevent users from deleting their own account
        if (userEmail != null && userEmail.equals(customer.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You cannot delete your own account while logged in");
        }
        
        customerRepository.delete(customer);
        System.out.println("Deleted customer with id: " + id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
