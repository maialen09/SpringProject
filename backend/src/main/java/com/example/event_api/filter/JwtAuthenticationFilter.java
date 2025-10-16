package com.example.event_api.filter;

import com.example.event_api.account.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
            String requestPath = request.getRequestURI();

            if (requestPath.startsWith("/account") || requestPath.contains("/h2-console")) {
                filterChain.doFilter(request, response);
                return;
            }
            if(requestPath.startsWith("/api/customers")){
                String authHeader = request.getHeader("Authorization");
                
                // Check if Authorization header exists and has correct format
                if (authHeader == null || !authHeader.startsWith("Bearer ")){
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Missing or invalid Authorization header\"}");
                    return;
                }
                
                // Extract and validate token
                String token = authHeader.substring(7); // Remove "Bearer "
                try{
                    String email = jwtUtil.extractEmail(token);
                    if(!jwtUtil.validateToken(token, email)){
                        // Token is invalid or expired
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
                        return;
                    }
                    // Token is valid, continue to controller
                    filterChain.doFilter(request, response);

                }catch(Exception e){
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Token validation failed: " + e.getMessage() + "\"}");
                    return;
                }
            } else {
                // For paths other than /api/customers, just continue
                filterChain.doFilter(request, response);
            }
    }

}


