package com.unit.oauth2Service.controller;

import com.unit.oauth2Service.model.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/private/user")
public class ProtegidoController {




    @GetMapping("/hola")
    public String privatePreuba(){


        return "Bienvenido";
    }


    @GetMapping("/me")
    public String getCurrentUser(UserPrincipal user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            // Si el principal es un objeto UserDetails
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                return userDetails.getUsername();
            }
            // Si el principal es un String (ej. JWT token)
            else if (authentication.getPrincipal() instanceof String) {
                return (String) authentication.getPrincipal();
            }
        }

        return null; // Si no hay un usuario autenticado
    }
}
