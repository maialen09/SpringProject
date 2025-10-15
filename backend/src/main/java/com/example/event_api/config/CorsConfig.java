package com.example.event_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        //allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        //allow req from frontend
        config.addAllowedOrigin("http://localhost:3000");
        
        //allow all headers
        config.addAllowedHeader("*");

        //allow all HTTP methods 
        config.addAllowedMethod("*");
        
        //apply this config to all endpoints
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
