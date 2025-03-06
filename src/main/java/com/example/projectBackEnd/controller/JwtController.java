package com.example.projectBackEnd.controller;

import com.example.projectBackEnd.dto.JwtRequest;
import com.example.projectBackEnd.dto.JwtResponse;
import com.example.projectBackEnd.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
//@RequestMapping("/api")
public class JwtController {
    private final JwtService jwtService;

    @Autowired
    public JwtController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping({"/login"})
    public JwtResponse crateJwtToken(@RequestBody JwtRequest jwtRequest)throws Exception{
        return jwtService.createJwtToken(jwtRequest);
    }
}
