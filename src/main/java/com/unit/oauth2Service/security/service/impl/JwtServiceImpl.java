package com.unit.oauth2Service.security.service.impl;

import com.unit.oauth2Service.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${key-signature}")
    private String keySignature;




    @Override
    public String extractUserName(String token) {


        return extractClaims(token, Claims::getSubject);
    }


    @Override
    public String generateToken(UserDetails userDetails) {

        return Jwts
                .builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+120000*10*10*10))
                .signWith(getSingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    //devuelve la clave con la que se firma el toquen
    private Key getSingKey() {
        byte[] key = Decoders.BASE64.decode(keySignature);
        return Keys.hmacShaKeyFor(key);
    }

    //Extrae el Payload del token, requiere firmarse para poder acceder al contenido
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()// Inicia un parser de JWT para analizar el token.
                .setSigningKey(getSingKey()) // Establece la clave de firma utilizada para verificar el token.
                .build() // Construye el parser configurado.
                .parseClaimsJws(token) // Analiza el token JWT y valida su firma.
                .getBody();  // Obtiene y retorna el cuerpo (claims) del token.
    }

    //Deveulve un objeto del body o tambien denominado un clain
    private <T> T extractClaims(String token, Function<Claims,T> claimsTResults){
        final Claims claims = extractAllClaims(token);

        return   claimsTResults.apply(claims);
    }

    //valida el token de la fecha de expiracion
    private boolean isTokenExpired(String token){

        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

}
