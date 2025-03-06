package com.example.projectBackEnd.dto;

import com.example.projectBackEnd.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String id;
    private String userName;
    private String address;
    private String email;
    private String tel;
    private Role role;
    private String image;
    private String password;
}
