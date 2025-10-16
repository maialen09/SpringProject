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
            String requestMethod = request.getMethod();

            //add CORS headers to all responses
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
            response.setHeader("Access-Control-Allow-Credentials", "true");

            //allow CORS preflight requests 
            if ("OPTIONS".equalsIgnoreCase(requestMethod)) {
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            if (requestPath.startsWith("/account") || requestPath.contains("/h2-console")) {
                filterChain.doFilter(request, response);
                return;
            }
            if(requestPath.startsWith("/api/customers")){
                String authHeader = request.getHeader("Authorization");
                
                //check if authorization header exists + has correct format
                if (authHeader == null || !authHeader.startsWith("Bearer ")){
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Missing or invalid Authorization header\"}");
                    return;
                }
                
                //extract and validate token
                String token = authHeader.substring(7); // Remove "Bearer "
                try{
                    String username = jwtUtil.extractEmail(token); // This actually extracts username, not email
                    
                    //check if token is not expired 
                    if(!jwtUtil.validateToken(token, username)){
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
                        return;
                    }
                    //token is valid
                    filterChain.doFilter(request, response);

                }catch(Exception e){
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Token validation failed: " + e.getMessage() + "\"}");
                    return;
                }
            } else {
                filterChain.doFilter(request, response);
            }
    }

}


