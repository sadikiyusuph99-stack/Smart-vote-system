package com.example.demo.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration 
@EnableWebSecurity 
public class SecurityConfig{

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http

        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/css/**","/js/**","/images/**","/uploads/**","/*.png","/form","/signup","/register","/idhinishakura","/terms","/submitVote","/success","/admin/**","/6","/error","/forgot-password","/reset-password").permitAll()
            .anyRequest().authenticated()
        );

    http.csrf(csrf -> csrf.disable());
    
    return http.build();
    }
   
}
