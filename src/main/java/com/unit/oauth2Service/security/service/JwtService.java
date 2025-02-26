package com.unit.oauth2Service.security.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    //extraye el usuario del token
    String extractUserName(String token);
    //generamos el token
    String generateToken(UserDetails userDetails);

    //validamos el token
    boolean validateToken(String token, UserDetails userDetails);
}
