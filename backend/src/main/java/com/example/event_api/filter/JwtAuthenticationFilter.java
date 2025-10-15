package com.example.event_api.filter;

import com.example.event_api.util.JwtUtil;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain){
        throws ServletException, IOException{
            String requestPath = request.getRequestURI();

            if (requestPath.contains("/account") || requestPath.contains("/h2-console")) {
                filterChain.doFilter(request, response);
                return;
            }
            if(requestPath.contains("/api/customers")){
                String authHeader = request.getHeader("Authorization");
                if (authHeader == null || !authHeader.contains("Bearer ")){
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Unauthorized\"}");
                    return;
                }
                String token = authHeader.substring(7); //removes the bearer string
                try{
                    String email = jwtUtil.extractEmail(token);
                    if(!jwtUtil.validateToken(token, email)){
                        //token invalid response
                        response.setStatus(401);
                        response.getWriter().write("{\"error\": \"Invalid token\"}");
                        return;
                    }
                    filterChain.doFilter(request, response); //only valid tokens

                }catch(Exception e){
                    response.setStatus(401);
                    response.getWriter().write("{\"error\": \"Token validation error\"}");
                }
            }
        }

    }
}