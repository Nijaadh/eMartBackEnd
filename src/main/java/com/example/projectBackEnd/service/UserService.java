package com.example.projectBackEnd.service;

import com.example.projectBackEnd.dto.UserDto;
import com.example.projectBackEnd.util.CommonResponse;

public interface UserService {
    CommonResponse saveUser(UserDto userDto);
    CommonResponse getAll();
    boolean isUserNameExists(String userName);
    boolean isEmailExists(String email);
    CommonResponse getUserById(Long id);
    CommonResponse updateUser(UserDto userDto);
    CommonResponse deleteUser(Long id);
}
