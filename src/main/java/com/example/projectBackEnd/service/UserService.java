package com.example.projectBackEnd.service;

import com.example.projectBackEnd.dto.UserDto;
import com.example.projectBackEnd.util.CommonResponse;

public interface UserService {
    boolean isUserNameExists(String userName);

    boolean isEmailExists(String email);

    CommonResponse saveUser(UserDto userDto);


    CommonResponse getAll();
}
