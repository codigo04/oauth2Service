package com.unit.oauth2Service.controller;


import com.unit.oauth2Service.dto.request.LoginReq;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/prueba/login")
public class AOth2Controller {


    @PostMapping("/sesion")
    public String login(@RequestBody LoginReq loginReq){

        return "autenticado";

    }
}
