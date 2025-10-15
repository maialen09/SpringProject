package com.example.event_api.dto;

public class LoginRequest {
    private String username;
    private String password;

    // Constructors
    public LoginRequest() {}

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Override Spring Boot's default user with
    // our own user, password, and authorities:
    @Bean
    public InMemoryUserDetailsManager users() {
        return new InMemoryUserDetailsManager(
            User.withUsername("client")
                .password("{noop}DoNotTell")
                .authorities("read")
                .build()
    );
    }

}