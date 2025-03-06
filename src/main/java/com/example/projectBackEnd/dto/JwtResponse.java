package com.example.projectBackEnd.dto;

import com.example.projectBackEnd.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JwtResponse {
    private User user;
    private String jwtToken;
}
